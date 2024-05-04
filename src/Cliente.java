import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cliente HTTP que maneja conexiones directas de socket para peticiones GET,
 * con concurrencia y manejo de recursos.
 */
public class Cliente {
    // Constante para el número máximo de hilos en el pool
    private static final int MAX_THREADS = 4;
    // Set para mantener un registro de las URLs visitadas
    private static final Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());
    // Lock para manejar el acceso concurrente al set de URLs visitados
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        // Array de URLs para ser descargadas
        String[] urls = {
                "http://148.204.58.221/axel/aplicaciones/sockets/java/file1.txt",
                "http://148.204.58.221/axel/aplicaciones/sockets/java/file2.txt",
                "http://148.204.58.221/axel/aplicaciones/sockets/java/file3.txt",
                "http://148.204.58.221/axel/aplicaciones/sockets/java/file1.txt" // URL repetido para prueba
        };

        // Creación de un pool de hilos para manejar las descargas de forma concurrente
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        // Bucle para asignar cada URL del array a una tarea de descarga
        for (String url : urls) {
            executor.execute(() -> {
                // Bloqueo para gestionar el acceso concurrente al conjunto de URLs visitadas
                lock.lock();
                try {
                    // Validación y adición de la URL al conjunto de visitados
                    if (visitedUrls.add(url)) {
                        // Desbloqueo antes de la descarga para no retener el bloqueo durante la descarga
                        lock.unlock();
                        // Llamada al método de descarga
                        downloadResource(url);
                    } else {
                        // Mensaje de URL ya visitada y desbloqueo
                        System.out.println("URL already visited, skipping download: " + url);
                        lock.unlock();
                    }
                } catch (Exception e) {
                    // Desbloqueo en caso de excepción
                    lock.unlock();
                    System.out.println("An error occurred while processing URL: " + url);
                    e.printStackTrace();
                }
            });
        }

        // Cierre del pool de hilos y espera a que finalicen todas las tareas
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            System.out.println("Interruption while waiting for tasks to finish.");
            Thread.currentThread().interrupt();
        }
    }

    // Método para descargar un recurso utilizando un socket directo
    private static void downloadResource(String urlString) {
        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        try {
            // Creación del objeto URL y extracción de componentes
            URL url = new URL(urlString);
            String host = url.getHost();
            int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();
            String resource = url.getFile().isEmpty() ? "/" : url.getFile();

            // Creación y conexión del socket
            socket = new Socket(host, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Formateo y envío de la petición GET
            writer.println("GET " + resource + " HTTP/1.1");
            writer.println("Host: " + host);
            writer.println("Connection: close");
            writer.println(); // Línea en blanco para finalizar los headers

            // Procesamiento de la respuesta del servidor
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            System.out.println("Download completed for: " + urlString);
            System.out.println(response.toString());
        } catch (IOException e) {
            // Gestión de excepciones
            System.out.println("Error downloading resource: " + urlString);
            e.printStackTrace();
        } finally {
            // Cierre adecuado de recursos
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources for URL: " + urlString);
            }
        }
    }
}
