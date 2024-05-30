// Importa la clase InputStream del paquete java.io para manejar flujos de entrada de datos.
import java.io.InputStream;
// Importa la clase HttpURLConnection del paquete java.net para manejar conexiones HTTP.
import java.net.HttpURLConnection;

// Define la clase HttpResponse que encapsula una respuesta HTTP.
public class HttpResponse {
    // Variable para almacenar los datos de la respuesta (contenido recibido).
    public InputStream data;
    // Variable para almacenar el código de estado HTTP de la respuesta.
    public int statusCode;
    // Variable para almacenar el mensaje de estado HTTP de la respuesta.
    public String statusMessage;
    // Variable para almacenar la conexión HTTP asociada con la respuesta.
    public HttpURLConnection connection;

    // Constructor de la clase que inicializa los atributos con los valores proporcionados.
    public HttpResponse(InputStream data, int statusCode, String statusMessage, HttpURLConnection connection) {
        this.data = data;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.connection = connection;
    }
}
