import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private ExecutorService executor;

    public ThreadPoolManager() {
        // Inicializar el pool con un número fijo de hilos.
        executor = Executors.newFixedThreadPool(10); // Pool de 10 hilos
    }

    public void execute(Runnable task) {
        // Ejecutar una tarea en el pool.
        executor.execute(task);
    }

    public void shutdown() {
        // Iniciar el proceso de cierre del pool.
        executor.shutdown();
        try {
            // Esperar a que todas las tareas se completen o el tiempo máximo sea alcanzado.
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                // Forzar la terminación de todas las tareas que aún se estén ejecutando.
                System.out.println("Forcing shutdown as tasks did not finish in the given time.");
                executor.shutdownNow();

                // Revisar si las tareas se terminaron forzosamente y no de forma natural.
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            // En caso de que el hilo actual sea interrumpido mientras espera.
            System.err.println("Current thread was interrupted while waiting for the tasks to finish.");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
