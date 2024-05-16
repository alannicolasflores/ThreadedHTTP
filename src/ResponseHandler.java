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

    public void handleResponse(String response, URL urlObj) {
        int statusCode = getStatusCode(response);
        String statusMessage = "Código de estado HTTP: " + statusCode + " para URL: " + urlObj;
        System.out.println("<p>" + statusMessage + "</p>");
        if (statusCode == 200) {
            handle200OK(response, urlObj);
        } else if (statusCode == 301 || statusCode == 302) {
            String newUrl = getLocationFromHeaders(response);
            if (newUrl != null) {
                System.out.println("<p>Redireccionado a: " + newUrl + "</p>");
                URLProcessor processor = new URLProcessor(executor, visitedURLs, cliente);
                processor.processURL(newUrl);
            }
        } else {
            System.out.println("<p>Código de estado HTTP no manejado: " + statusCode + "</p>");
        }
    }

    public void handle200OK(String response, URL urlObj) {
        String content = getContent(response);
        if (content.isEmpty()) {
            System.out.println("<p>Contenido recibido de " + urlObj + ": No Content</p>");
        } else {
            System.out.println("<p>Contenido recibido de " + urlObj + ": " + content.substring(0, Math.min(200, content.length())) + "</p>");
        }
        if (isDirectory(content)) {
            List<String> links = LinkExtractor.extractLinks(urlObj.toString(), content);
            for (String link : links) {
                URLProcessor processor = new URLProcessor(executor, visitedURLs, cliente);
                processor.processURL(link);
            }
        } else {
            saveToFile(urlObj, content);
        }
    }

    public String getLocationFromHeaders(String response) {
        String[] headers = response.split("\r\n");
        for (String header : headers) {
            if (header.startsWith("Location:")) {
                return header.split(" ")[1].trim();
            }
        }
        return null;
    }

    public void saveToFile(URL url, String content) {
        try {
            String path = url.getPath();
            if (path.endsWith("/")) {
                return; // No guardar directorios como archivos
            }
            java.nio.file.Path filePath = Paths.get("descargas", path.substring(1)); // Eliminar la primera barra
            Files.createDirectories(filePath.getParent()); // Crear directorios si no existen
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

    private boolean isDirectory(String content) {
        return content.contains("<html>") && content.contains("<a ");
    }
}
