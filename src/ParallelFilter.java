import java.awt.image.BufferedImage;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class ParallelFilter extends RecursiveAction {
    private BufferedImage image;
    private int startRow;
    private int endRow;

    // PCAM - Aglomeración: Umbral para evitar crear demasiados hilos pequeños
    private static final int THRESHOLD = 100;

    public ParallelFilter(BufferedImage image, int start, int end) {
        this.image = image;
        this.startRow = start;
        this.endRow = end;
    }

    @Override
    protected void compute() {
        // Divide y Vencerás:
        if ((endRow - startRow) < THRESHOLD) {
            computeDirectly(); // Caso Base
        } else {
            // PCAM - Particionamiento: Dividir filas a la mitad
            int mid = (startRow + endRow) / 2;

            ParallelFilter topHalf = new ParallelFilter(image, startRow, mid);
            ParallelFilter bottomHalf = new ParallelFilter(image, mid, endRow);

            // PCAM - Mapeo: Work-Stealing en el Pool
            invokeAll(topHalf, bottomHalf);
        }
    }

    // Lógica secuencial del filtro (Invertir colores)
    private void computeDirectly() {
        int width = image.getWidth();
        for (int y = startRow; y < endRow; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                int a = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                // Efecto Negativo
                r = 255 - r;
                g = 255 - g;
                b = 255 - b;

                int newRgb = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, newRgb);
            }
        }
    }

    // Método estático para lanzar el proceso
    public static void processImage(BufferedImage image) {
        int processors = Runtime.getRuntime().availableProcessors();
        ForkJoinPool pool = new ForkJoinPool(processors);

        System.out.println("Starting processing with " + processors + " logical cores.");

        ParallelFilter rootTask = new ParallelFilter(image, 0, image.getHeight());

        long startTime = System.currentTimeMillis();
        pool.invoke(rootTask);
        long endTime = System.currentTimeMillis();

        System.out.println("Processing completed in: " + (endTime - startTime) + " ms");
    }
}