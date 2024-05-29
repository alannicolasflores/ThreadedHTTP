import java.io.InputStream;
import java.net.HttpURLConnection;

public class HttpResponse {
    public InputStream data;
    public int statusCode;
    public String statusMessage;
    public HttpURLConnection connection;

    public HttpResponse(InputStream data, int statusCode, String statusMessage, HttpURLConnection connection) {
        this.data = data;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.connection = connection;
    }
}
