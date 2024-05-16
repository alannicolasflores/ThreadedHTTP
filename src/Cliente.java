import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Arrays;

public class Cliente {
    private ExecutorService executor;
    private Set<String> visitedURLs;

    public Cliente() {
        this.executor = Executors.newFixedThreadPool(10); // Puedes ajustar el tamaño del pool
        this.visitedURLs = new HashSet<>();
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        List<String> initialUrls = Arrays.asList(
                "http://148.204.58.221/axel/aplicaciones/sockets/java/canales/flujo/",
                "http://148.204.58.221/axel/aplicaciones/sockets/java/DatagramPacket.txt"
        );
        cliente.processInitialUrls(initialUrls);
    }

    public void processInitialUrls(List<String> urls) {
        System.out.println("<html><body>");
        List<String> urlsNoDescargadas = verificarUrlsNoDescargadas(urls);
        for (String url : urlsNoDescargadas) {
            if (isValidURL(url)) {
                URLProcessor processor = new URLProcessor(executor, visitedURLs, this);
                processor.processURL(url);
            } else {
                System.out.println("<p>Formato de URL incorrecto: " + url + "</p>");
            }
        }
        System.out.println("</body></html>");
    }

    public List<String> verificarUrlsNoDescargadas(List<String> urls) {
        return urls.stream().filter(url -> !isDownloaded(url)).collect(Collectors.toList());
    }

    public boolean isDownloaded(String url) {
        try {
            String path = url.substring(url.indexOf("://") + 3); // Remover el protocolo
            return Files.exists(Paths.get("descargas", path));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidURL(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
