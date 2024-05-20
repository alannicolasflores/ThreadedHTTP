import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class URLProcessor {
    private ExecutorService executor;
    private Set<String> visitedURLs;
    private Cliente cliente;

    public URLProcessor(ExecutorService executor, Set<String> visitedURLs, Cliente cliente) {
        this.executor = executor;
        this.visitedURLs = visitedURLs;
        this.cliente = cliente;
    }

    public void processURL(String url) {
        if (!visitedURLs.contains(url)) {
            visitedURLs.add(url);
            executor.submit(() -> {
                try {
                    URL urlObj = new URL(url);
                    String host = urlObj.getHost();
                    String file = urlObj.getFile();
                    boolean isDirectory = file.endsWith("/");

                    try (SocketManager socketManager = new SocketManager(host, 80)) {
                        socketManager.sendGetRequest(file, host);
                        String response = socketManager.readResponse();
                        ResponseHandler handler = new ResponseHandler(executor, visitedURLs, cliente);
                        handler.handleResponse(response, urlObj, isDirectory, file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
