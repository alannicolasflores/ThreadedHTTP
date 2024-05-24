import java.io.InputStream;

public class HttpResponse {
    public InputStream data;
    public int statusCode;
    public String statusMessage;

    public HttpResponse(InputStream data, int statusCode, String statusMessage) {
        this.data = data;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}