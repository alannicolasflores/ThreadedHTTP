import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessor {
    private SocketManager socketManager = new SocketManager();
    private FileManager fileManager = new FileManager();

    public void processURL(String url) {
        HttpResponse response = socketManager.sendRequest(url);
        if (response.statusCode == HttpURLConnection.HTTP_OK) {
            if (url.endsWith("/")) {
                processDirectory(response.data, url);
            } else {
                fileManager.saveFile(response.data, url);
                fetchAndSaveHTMLForFile(url);  // Función para descargar el HTML del directorio padre del archivo
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 200</h1><h2>Status Message: OK</h2><div>File fetched and saved successfully for " + url + "</div></body></html>");
            }
        } else {
            System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + response.statusCode + "</h1><h2>Status Message: " + response.statusMessage + "</h2><div>Error: Content not available</div></body></html>");
        }
    }
    private void fetchAndSaveHTMLForFile(String fileUrl) {
        try {
            String directoryUrl = fileUrl.substring(0, fileUrl.lastIndexOf('/') + 1);
            HttpResponse directoryResponse = socketManager.sendRequest(directoryUrl);
            if (directoryResponse.statusCode == HttpURLConnection.HTTP_OK) {
                String htmlContent = streamToString(directoryResponse.data);
                fileManager.saveFile(new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)), directoryUrl + "index.html");
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 200</h1><h2>Status Message: OK</h2><div>HTML for directory fetched and saved successfully for " + directoryUrl + "</div></body></html>");
            } else {
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + directoryResponse.statusCode + "</h1><h2>Status Message: " + directoryResponse.statusMessage + "</h2><div>Error fetching HTML for directory: " + directoryUrl + "</div></body></html>");
            }
        } catch (Exception e) {
            System.out.println("Error fetching HTML for directory: " + e.getMessage());
        }
    }
    private void processDirectory(InputStream data, String url) {
        try {
            String htmlContent = streamToString(data);
            fileManager.saveFile(new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)), url + "index.html");
            System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 200</h1><h2>Status Message: OK</h2><div>HTML fetched and saved successfully for " + url + "</div></body></html>");

            List<String> links = extractLinks(url, htmlContent);
            for (String link : links) {
                processURL(link);  // Procesar recursivamente cada enlace
            }
        } catch (Exception e) {
            System.out.println("Error processing directory: " + e.getMessage());
        }
    }

    private String streamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    public static List<String> extractLinks(String baseUrl, String html) {
        List<String> links = new ArrayList<>();
        Matcher matcher = Pattern.compile("<a\\s+href=\"([^\"]+)\">([^<]+)</a>", Pattern.CASE_INSENSITIVE).matcher(html);
        while (matcher.find()) {
            String url = matcher.group(1).trim();
            if (matcher.group(2).trim().equals("Parent Directory")) {
                continue;
            }
            String fullUrl = url.startsWith("http") ? url : baseUrl + (baseUrl.endsWith("/") ? "" : "/") + url;
            if (url.endsWith("/") || url.matches(".*\\.(java|txt|zip|pdf|doc|docx|ppt|pptx|xls|xlsx|png|jpg|jpeg|gif|mp3|mp4|avi|mkv)$")) {
                links.add(fullUrl);
            }
        }
        return links;
    }
}
