import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {
    private FileProcessor fileProcessor = new FileProcessor();
    private SocketManager socketManager = new SocketManager();
    private FileManager fileManager = new FileManager();

    public void download(String url) {
     //   System.out.println("Downloading: " + url);
        HttpResponse response = socketManager.sendRequest(url);
        switch (response.statusCode) {
            case HttpURLConnection.HTTP_OK:
                fileProcessor.processURL(url);
                break;
            case HttpURLConnection.HTTP_FORBIDDEN:
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 403</h1><h2>Status Message: Forbidden</h2><div>Error: Access denied for " + url + "</div></body></html>");
                break;
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
                String newUrl = response.connection.getHeaderField("Location");
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + response.statusCode + "</h1><h2>Status Message: Redirected</h2><div>Redirecting to " + newUrl + "</div></body></html>");
                download(newUrl); // Follow the redirect
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 404</h1><h2>Status Message: Not Found</h2><div>Error: " + url + " not found</div></body></html>");
                break;
            default:
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + response.statusCode + "</h1><h2>Status Message: " + response.statusMessage + "</h2><div>Error: No content available</div></body></html>");
        }
        fetchHTML(url); // This needs to be revised if it should only fetch for directories or handle errors differently
    }

    public void fetchHTML(String url) {
       // System.out.println("Fetching HTML for: " + url);
        HttpResponse response = socketManager.sendRequest(url);
        if (response.statusCode == HttpURLConnection.HTTP_OK) {
            fileManager.saveFile(response.data, url); // Check if this should save as 'index.html'
            System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 200</h1><h2>Status Message: OK</h2><div>HTML fetched successfully for " + url + "</div></body></html>");
        } else {
            System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + response.statusCode + "</h1><h2>Status Message: " + response.statusMessage + "</h2><div>Failed to fetch HTML for: " + url + "</div></body></html>");
        }
    }
}
