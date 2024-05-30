import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessor {
    // Instancias de otras clases que se utilizarán en este proceso
    private SocketManager socketManager = new SocketManager();  // Maneja las conexiones de red
    private FileManager fileManager = new FileManager();  // Maneja archivos locales

    // Método para procesar una URL dada
    public void processURL(String url) {
        HttpResponse response = socketManager.sendRequest(url);  // Enviar solicitud HTTP
        if (response.statusCode == HttpURLConnection.HTTP_OK) {  // Si la solicitud es exitosa
            if (url.endsWith("/")) {
                processDirectory(response.data, url);  // Procesar directorio si la URL termina en '/'
            } else {
                fileManager.saveFile(response.data, url);  // Guardar archivo si la URL no termina en '/'
                fetchAndSaveHTMLForFile(url);  // Descargar HTML del directorio padre del archivo
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 200</h1><h2>Status Message: OK</h2><div>File fetched and saved successfully for " + url + "</div></body></html>");
            }
        } else {
            handleHttpError(response);  // Manejar errores HTTP
        }
    }

    // Método para descargar y guardar el HTML del directorio padre del archivo
    private void fetchAndSaveHTMLForFile(String fileUrl) {
        try {
            String directoryUrl = fileUrl.substring(0, fileUrl.lastIndexOf('/') + 1);  // Obtener URL del directorio padre
            HttpResponse directoryResponse = socketManager.sendRequest(directoryUrl);  // Enviar solicitud HTTP al directorio padre
            if (directoryResponse.statusCode == HttpURLConnection.HTTP_OK) {
                String htmlContent = streamToString(directoryResponse.data);  // Convertir el contenido HTML en String
                fileManager.saveFile(new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)), directoryUrl + "index.html");  // Guardar HTML como index.html
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 200</h1><h2>Status Message: OK</h2><div>HTML for directory fetched and saved successfully for " + directoryUrl + "</div></body></html>");
            } else {
                handleHttpError(directoryResponse);  // Manejar errores HTTP
            }
        } catch (Exception e) {
            System.out.println("Error fetching HTML for directory: " + e.getMessage());
        }
    }

    // Método para manejar errores HTTP
    private void handleHttpError(HttpResponse response) {
        switch (response.statusCode) {
            case HttpURLConnection.HTTP_FORBIDDEN:
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 403</h1><h2>Status Message: Forbidden</h2><div>Error: Access denied</div></body></html>");
                break;
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + response.statusCode + "</h1><h2>Status Message: Redirected</h2><div>Error: Redirection not handled in this context</div></body></html>");
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 404</h1><h2>Status Message: Not Found</h2><div>Error: Resource not found</div></body></html>");
                break;
            default:
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + response.statusCode + "</h1><h2>Status Message: " + response.statusMessage + "</h2><div>Error: Content not available</div></body></html>");
        }
    }

    // Método para procesar un directorio
    private void processDirectory(InputStream data, String url) {
        try {
            String htmlContent = streamToString(data);  // Convertir el contenido HTML en String
            fileManager.saveFile(new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)), url + "index.html");  // Guardar HTML como index.html
            System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 200</h1><h2>Status Message: OK</h2><div>HTML fetched and saved successfully for " + url + "</div></body></html>");

            List<String> links = extractLinks(url, htmlContent);  // Extraer enlaces del HTML
            for (String link : links) {
                processURL(link);  // Procesar recursivamente cada enlace
            }
        } catch (Exception e) {
            System.out.println("Error processing directory: " + e.getMessage());
        }
    }

    // Método para convertir un InputStream a String
    private String streamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    // Método estático para extraer enlaces de un HTML dado
    public static List<String> extractLinks(String baseUrl, String html) {
        List<String> links = new ArrayList<>();
        Matcher matcher = Pattern.compile("<a\\s+href=\"([^\"]+)\">([^<]+)</a>", Pattern.CASE_INSENSITIVE).matcher(html);  // Patrón regex para encontrar enlaces
        while (matcher.find()) {
            String url = matcher.group(1).trim();
            if (matcher.group(2).trim().equals("Parent Directory")) {
                continue;  // Ignorar enlaces al "Parent Directory"
            }
            String fullUrl = url.startsWith("http") ? url : baseUrl + (baseUrl.endsWith("/") ? "" : "/") + url;
            if (url.endsWith("/") || url.matches(".*\\.(java|txt|zip|pdf|doc|docx|ppt|pptx|xls|xlsx|png|jpg|jpeg|gif|mp3|mp4|avi|mkv)$")) {
                links.add(fullUrl);  // Añadir solo enlaces que terminen en '/' o correspondan a ciertas extensiones de archivo
            }
        }
        return links;
    }
}
