import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;



public class SocketManager {
    public HttpResponse sendRequest(String url) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            String message = connection.getResponseMessage();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return new HttpResponse(connection.getInputStream(), responseCode, "OK");
            } else {
                return new HttpResponse(null, responseCode, message);
            }
        } catch (Exception e) {
            return new HttpResponse(null, 500, "Internal Server Error: " + e.getMessage());
        }
    }
}