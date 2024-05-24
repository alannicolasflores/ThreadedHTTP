import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> initialUrls = Arrays.asList(
                "http://148.204.58.221/axel/aplicaciones/sockets/java/",  // URL existente, debería devolver 200 OK
                "http://148.204.58.221/axel/aplicaciones/sockets/java/noexist.html",  // URL inexistente, debería devolver 404 Not Found
                "http://148.204.58.221/axel/aplicaciones/sockets/c/client.c",  // URL de un archivo, debería devolver 200 OK
                "http://148.204.58.221/axel/aplicaciones/sockets/java/restricted/",  // URL restringida, potencialmente podría devolver 403 Forbidden
                "http://148.204.58.221/axel/aplicaciones/sockets/java/redirect"  // URL que podría redirigir, para probar manejo de redirecciones

        );

        // Instanciar clases necesarias
        URLValidator validator = new URLValidator();
        Downloader downloader = new Downloader();
        ThreadPoolManager threadPool = new ThreadPoolManager();
        URLTracker urlTracker = new URLTracker();

        // Proceso de validación y descarga
        for (String url : initialUrls) {
            if (validator.validate(url)) {
                if (!urlTracker.isVisited(url)) {
                    urlTracker.markVisited(url);
                    threadPool.execute(() -> downloader.download(url));
                }
            }
        }

        // Finalizar el ThreadPool
        threadPool.shutdown();
    }
}