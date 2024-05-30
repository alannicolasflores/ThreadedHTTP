// Importa la clase URL desde el paquete java.net, utilizada para manipular URLs.
import java.net.URL;
// Importa la clase HttpURLConnection desde el paquete java.net, utilizada para manejar conexiones HTTP.
import java.net.HttpURLConnection;

// Define la clase SocketManager que se encarga de gestionar las conexiones de red y enviar solicitudes HTTP.
public class SocketManager {
    // Método sendRequest que acepta una URL como string y devuelve un objeto HttpResponse.
    public HttpResponse sendRequest(String url) {
        // Bloque try para manejar posibles excepciones al realizar la conexión HTTP.
        try {
            // Crea un objeto URL a partir de la cadena de texto proporcionada.
            URL urlObj = new URL(url);
            // Abre una conexión HTTP y la castea a HttpURLConnection.
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            // Establece el método de la solicitud a "GET".
            connection.setRequestMethod("GET");
            // Obtiene el código de respuesta de la conexión HTTP.
            int responseCode = connection.getResponseCode();
            // Obtiene el mensaje de la respuesta de la conexión HTTP.
            String message = connection.getResponseMessage();
            // Verifica si el código de respuesta indica que la solicitud fue exitosa (HTTP_OK).
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Retorna un nuevo HttpResponse con el stream de entrada (contenido recibido), código de respuesta, mensaje "OK", y la conexión.
                return new HttpResponse(connection.getInputStream(), responseCode, "OK", connection);
            } else {
                // Retorna un HttpResponse con null como contenido, el código de respuesta, el mensaje de error, y la conexión.
                return new HttpResponse(null, responseCode, message, connection);
            }
        } catch (Exception e) {
            // En caso de excepciones, retorna un HttpResponse con null como contenido, código 500, mensaje de error interno y sin conexión.
            return new HttpResponse(null, 500, "Internal Server Error: " + e.getMessage(), null);
        }
    }
}
