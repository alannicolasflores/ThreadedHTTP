import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Lista de URLs iniciales para procesar
        List<String> initialUrls = Arrays.asList(
                "http://148.204.58.221/axel/aplicaciones/sockets/java/",  // URL de un directorio, debería devolver 200 OK
                "http://148.204.58.221/axel/aplicaciones/sockets/c/client.c",  // URL de un archivo, debería devolver 200 OK
                "http://httpstat.us/403" , // URL restringida, debería devolver 403 Forbidden
                "http://httpstat.us/301", // URL que redirige permanentemente
                "http://httpstat.us/302",  // URL que redirige temporalmente
                "http://148.204.58.221/axel/aplicaciones/sockets/java/noexist.html"  // URL inexistente, debería devolver 404 Not Found
        );

        // Instanciación de las clases necesarias para el proceso
        URLValidator validator = new URLValidator();
        Downloader downloader = new Downloader();
        ThreadPoolManager threadPool = new ThreadPoolManager();
        URLTracker urlTracker = new URLTracker();

        // Proceso de validación y descarga de URLs
        for (String url : initialUrls) {
            if (validator.validate(url)) {  // Validar si la URL es correcta
                if (!urlTracker.isVisited(url)) {  // Verificar si la URL ya ha sido visitada
                    urlTracker.markVisited(url);  // Marcar la URL como visitada
                    threadPool.execute(() -> downloader.download(url));  // Descargar la URL usando un hilo del pool
                }
            }
        }

        // Finalizar el ThreadPool una vez que todas las tareas han sido completadas
        threadPool.shutdown();
    }
}
