import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Cargar imagen 'input.jpg' o crear una de prueba
            BufferedImage image = loadOrCreateImage("input.jpg");

            System.out.println("Image Size: " + image.getWidth() + "x" + image.getHeight());

            // Ejecutar Filtro Paralelo
            ParallelFilter.processImage(image);

            // Guardar resultado
            File outputFile = new File("output_negative.jpg");
            ImageIO.write(image, "jpg", outputFile);
            System.out.println("Saved to: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static BufferedImage loadOrCreateImage(String path) throws IOException {
        File f = new File(path);
        if (f.exists()) {
            return ImageIO.read(f);
        } else {
            System.out.println("Image " + path + " not found. Creating a test image...");
            BufferedImage img = new BufferedImage(4000, 4000, BufferedImage.TYPE_INT_RGB);
            for(int x=0; x<4000; x++) {
                for(int y=0; y<4000; y++) {
                    img.setRGB(x, y, (x*y)%16777215);
                }
            }
            return img;
        }
    }
}