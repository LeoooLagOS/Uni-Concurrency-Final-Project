# 🖼️ Parallel Image Filter (PCAM Implementation)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Concurrency](https://img.shields.io/badge/Concurrency-Fork%2FJoin-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

Este proyecto implementa un algoritmo de procesamiento de imágenes de alto rendimiento utilizando **Java Fork/Join Framework**. El objetivo es aplicar un filtro (Inversión de Colores / Negativo) a imágenes de alta resolución, demostrando la eficiencia del paralelismo de datos frente al procesamiento secuencial.

El diseño del algoritmo sigue estrictamente la metodología **PCAM** (Partitioning, Communication, Agglomeration, Mapping).

---

## 🚀 Características

* **Procesamiento Paralelo:** Utiliza `RecursiveAction` para dividir la tarea en múltiples hilos.
* **Balanceo de Carga:** Implementa **Work-Stealing** a través de `ForkJoinPool` para maximizar el uso de la CPU.
* **Eficiencia:** Capaz de procesar imágenes 4K (16MP) en milisegundos.
* **Umbral Dinámico:** Utiliza un límite (`THRESHOLD`) para controlar la granularidad de las tareas y evitar el overhead de hilos.

---

## 🧠 Diseño del Algoritmo (Metodología PCAM)

El núcleo de este proyecto es la transición de una lógica secuencial a una paralela mediante cuatro etapas:

### 1. 🧩 Partitioning (Particionamiento)
El dominio de datos (la imagen) se descompone en su unidad más lógica: **Filas de píxeles**.
* **Lógica:** Cada fila puede ser procesada independientemente de las demás.
* **Implementación:** El algoritmo divide recursivamente el rango de filas `[startRow, endRow)` a la mitad.

### 2. 🗣️ Communication (Comunicación)
Se minimiza la comunicación para evitar cuellos de botella.
* **Estrategia:** Memoria Compartida.
* **Flujo:** El hilo maestro ("Scatter") pasa la referencia de la imagen y los índices a los trabajadores. Al finalizar ("Gather"), los cambios se reflejan directamente en la memoria compartida, eliminando la necesidad de paso de mensajes costoso.

### 3. 📦 Agglomeration (Aglomeración)
Crear un hilo por cada fila sería ineficiente.
* **Estrategia:** Agrupación por Bloques.
* **Implementación:** Se define un `THRESHOLD = 100`. Si un bloque tiene menos de 100 filas, se procesa secuencialmente en un solo hilo. Esto asegura que la carga de trabajo justifique el costo de creación del hilo.

### 4. 🗺️ Mapping (Mapeo)
Asignación de tareas lógicas a núcleos físicos.
* **Implementación:** Se utiliza `ForkJoinPool` inicializado con `Runtime.getRuntime().availableProcessors()`.
* **Comportamiento:** El pool asigna dinámicamente las tareas aglomeradas a los núcleos disponibles, utilizando "robo de trabajo" para mantener todos los núcleos ocupados.

---

## 📂 Estructura del Proyecto

```text
Final-Project/
├── src/
│   ├── ParallelFilter.java  # Lógica PCAM (RecursiveAction)
│   └── Main.java            # Punto de entrada y gestión de E/S
├── input.jpg                # Imagen de entrada (opcional)
├── output_negative.jpg      # Resultado generado
└── README.md                # Documentación
```

## 🛠️ Instalación y Ejecución
Requisitos
* Java JDK 8 o superior (Recomendado JDK 21).

* Git.

Pasos
1. Clonar el repositorio

```Bash

git clone [https://github.com/LeoooLagOS/Uni-Concurrency-Final-Project.git](https://github.com/LeoooLagOS/Uni-Concurrency-Final-Project.git)
cd Uni-Concurrency-Final-Project
```
2. Preparar la imagen (Opcional) Coloca una imagen llamada input.jpg en la carpeta raíz. Si no lo haces, el programa generará una imagen de prueba automáticamente.

3. Compilar

```Bash

javac src/*.java
```
3. Ejecutar

```Bash

java -cp . src.Main
# O si estás en Windows y tienes problemas con el classpath:
java src.Main
```
## 📊 Resultados Esperados
Al ejecutar el programa, verás una salida en consola indicando los núcleos utilizados y el tiempo de ejecución:

```Plaintext

Image Size: 4000x4000
Starting processing with 12 logical cores.
Processing completed in: 242 ms
Saved to: .../Final-Project/output_negative.jpg
```
