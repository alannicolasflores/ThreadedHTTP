import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Cliente {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        String url1 = "http://148.204.58.221/axel/aplicaciones/sockets/java/canales/flujo";
        String url2 = "http://148.204.58.221/axel/aplicaciones/sockets/java/DatagramPacket.txt";

        executor.submit(() -> downloadResource(url1));
        executor.submit(() -> downloadResource(url2));
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("Interruption while waiting for tasks to finish.");
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }

    private static void downloadResource(String urlString) {
        if (!addUrlToVisited(urlString)) {
            System.out.println("URL already visited, skipping download: " + urlString);
            return;
        }

        try (Socket socket = new Socket(new URL(urlString).getHost(), new URL(urlString).getPort() != -1 ? new URL(urlString).getPort() : 80);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String resource = new URL(urlString).getFile().isEmpty() ? "/" : new URL(urlString).getFile();
            writer.println("GET " + resource + " HTTP/1.1");
            writer.println("Host: " + new URL(urlString).getHost());
            writer.println("Connection: close");
            writer.println();

            String statusLine = reader.readLine();
            Map<String, String> headers = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] header = line.split(": ", 2);
                if (header.length == 2) {
                    headers.put(header[0], header[1]);
                }
            }

            StringBuilder responseBody = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseBody.append(line).append("\n");
            }

            int statusCode = Integer.parseInt(statusParts(statusLine)[1]);
            handleResponse(statusCode, urlString, headers, responseBody.toString());
        } catch (IOException e) {
            System.err.println("Error downloading resource: " + urlString);
            e.printStackTrace();
        }
    }

    private static void handleResponse(int statusCode, String url, Map<String, String> headers, String responseBody) {
        StringBuilder htmlOutput = new StringBuilder();
        htmlOutput.append("<html><head><title>Download Status</title></head><body>");
        htmlOutput.append("<h1>Download Result for: ").append(url).append("</h1>");
        htmlOutput.append("<p>Status Code: <strong>").append(statusCode).append("</strong></p>");

        switch (statusCode) {
            case 200:
                saveToFile(url, responseBody);
                htmlOutput.append("<p>Download successful. File has been saved.</p>");
                List<String> links = extractLinks(url, responseBody);
                links.forEach(link -> executor.submit(() -> downloadResource(link)));
                break;
            case 301:
            case 302:
                String newLocation = headers.get("Location");
                executor.submit(() -> downloadResource(newLocation));
                htmlOutput.append(String.format("<p>Redirected to: <a href='%s'>%s</a></p>", newLocation, newLocation));
                break;
            case 403:
                htmlOutput.append("<p>Access forbidden to this resource.</p>");
                break;
            case 404:
                htmlOutput.append("<p>Resource not found.</p>");
                break;
            default:
                htmlOutput.append("<p>Unhandled status code.</p>");
                break;
        }

        htmlOutput.append("</body></html>");
        System.out.println(htmlOutput.toString());
        saveHtmlOutput(htmlOutput.toString(), url);
    }

    private static void saveHtmlOutput(String htmlContent, String baseUrl) {
        try {
            URL url = new URL(baseUrl);
            String fileName = "/download_status_report.html";
            File file = new File("." + fileName);  // Save in the current directory
            file.getParentFile().mkdirs();  // Create directories if they do not exist
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(htmlContent);
            }
        } catch (IOException e) {
            System.err.println("Failed to save HTML output from: " + baseUrl);
            e.printStackTrace();
        }
    }

    private static boolean addUrlToVisited(String url) {
        return visitedUrls.add(url);
    }

    private static List<String> extractLinks(String baseUrl, String html) {
        List<String> links = new ArrayList<>();
        Matcher matcher = Pattern.compile("href=\"(.*?)\"", Pattern.CASE_INSENSITIVE).matcher(html);
        while (matcher.find()) {
            String url = matcher.group(1);
            if (!url.startsWith("http")) {
                try {
                    URL base = new URL(baseUrl);
                    URL link = new URL(base, url);
                    url = link.toString();
                } catch (MalformedURLException e) {
                    continue;
                }
            }
            links.add(url);
        }
        return links;
    }
    private static void saveToFile(String urlString, String content) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath();
            if (path.isEmpty() || path.equals("/")) {
                path = "/index.html";  // Default to index.html if no specific file is found
            }
            File file = new File("." + path);  // Save in the current directory
            file.getParentFile().mkdirs();  // Create directories if they do not exist
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(content);
            }
        } catch (IOException e) {
            System.err.println("Failed to save content from: " + urlString);
            e.printStackTrace();
        }
    }

    private static String[] statusParts(String statusLine) throws IOException {
        String[] parts = statusLine.split(" ");
        if (parts.length < 3) {
            throw new IOException("Invalid response status line: " + statusLine);
        }
        return parts;
    }
}
