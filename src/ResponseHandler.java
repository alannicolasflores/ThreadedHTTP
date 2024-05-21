import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class ResponseHandler {
    private ExecutorService executor;
    private Set<String> visitedURLs;
    private Cliente cliente;

    public ResponseHandler(ExecutorService executor, Set<String> visitedURLs, Cliente cliente) {
        this.executor = executor;
        this.visitedURLs = visitedURLs;
        this.cliente = cliente;
    }

    public void handleResponse(String response, URL urlObj, boolean isDirectory, String pathToFetch) {
        int statusCode = getStatusCode(response);
        String statusMessage = "Código de estado HTTP: " + statusCode + " para URL: " + urlObj;
        System.out.println("<p>" + statusMessage + "</p>");

        if (statusCode == 200) {
            handle200OK(response, urlObj, isDirectory, pathToFetch);
        } else if (statusCode == 301 || statusCode == 302) {
            String newUrl = getLocationFromHeaders(response);
            if (newUrl != null) {
                System.out.println("<p>Redireccionado a: " + newUrl + "</p>");
                URLProcessor processor = new URLProcessor(executor, visitedURLs, cliente);
                processor.processURL(newUrl);
            }
        } else if (statusCode == 403) {
            System.out.println("<p>Acceso prohibido a: " + urlObj + "</p>");
        } else if (statusCode == 404) {
            System.out.println("<p>Recurso no encontrado: " + urlObj + "</p>");
        } else {
            System.out.println("<p>Código de estado HTTP no manejado: " + statusCode + "</p>");
        }
    }

    public void handle200OK(String response, URL urlObj, boolean isDirectory, String pathToFetch) {
        String content = getContent(response);
        if (content.isEmpty()) {
            System.out.println("<p>Contenido recibido de " + urlObj + " vacío.</p>");
        } else {
            System.out.println("<p>Contenido recibido de " + urlObj + ": " + content.substring(0, Math.min(200, content.length())) + "</p>");
        }
        if (isDirectory) {
            List<String> links = LinkExtractor.extractLinks(urlObj.toString(), content);
            for (String link : links) {
                URLProcessor processor = new URLProcessor(executor, visitedURLs, cliente);
                processor.processURL(link);
            }
            saveToFile(urlObj, content, true, pathToFetch);
        } else {
            saveToFile(urlObj, content, false, pathToFetch);
            handleParentDirectory(urlObj); // Llamar al método para manejar el directorio padre
        }
    }

    public void handleParentDirectory(URL url) {
        if (!url.getPath().endsWith("/")) {
            try {
                URL parentUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1));
                System.out.println("<p>Fetching parent directory: " + parentUrl + "</p>");
                try (SocketManager socketManager = new SocketManager(parentUrl.getHost(), parentUrl.getPort() == -1 ? 80 : parentUrl.getPort())) {
                    socketManager.sendGetRequest(parentUrl.getPath(), parentUrl.getHost());
                    String parentResponse = socketManager.readResponse();
                    String parentContent = getContent(parentResponse);
                    if (!parentContent.isEmpty()) {
                        saveToFile(parentUrl, parentContent, true, parentUrl.getPath());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveToFile(URL url, String content, boolean isDirectory, String pathToFetch) {
        try {
            String path = url.getPath();
            java.nio.file.Path filePath;
            if (isDirectory) {
                filePath = Paths.get("descargas", path.substring(1), "index.html");
            } else {
                filePath = Paths.get("descargas", path.substring(1));
            }
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.getBytes(), StandardOpenOption.CREATE);
            System.out.println("<p>Archivo guardado: " + filePath.toString() + "</p>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getStatusCode(String response) {
        String[] lines = response.split("\r\n");
        if (lines.length > 0) {
            String[] statusLine = lines[0].split(" ");
            if (statusLine.length > 1) {
                try {
                    return Integer.parseInt(statusLine[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    private String getContent(String response) {
        int index = response.indexOf("\r\n\r\n");
        if (index != -1) {
            return response.substring(index + 4);
        }
        return "";
    }

    private String getLocationFromHeaders(String response) {
        String[] headers = response.split("\r\n");
        for (String header : headers) {
            if (header.startsWith("Location:")) {
                return header.split(" ")[1].trim();
            }
        }
        return null;
    }
}
