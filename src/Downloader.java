import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {
    private FileProcessor fileProcessor = new FileProcessor(); // Se instancia un objeto de la clase FileProcessor para procesar archivos.
    private SocketManager socketManager = new SocketManager(); // Se instancia un objeto de la clase SocketManager para gestionar conexiones de red.
    private FileManager fileManager = new FileManager(); // Se instancia un objeto de la clase FileManager para gestionar archivos.

    /**
     * Descarga el contenido de una URL y procesa la respuesta.
     *
     * @param url La URL a descargar.
     */
    public void download(String url) {
        //   System.out.println("Downloading: " + url);
        HttpResponse response = socketManager.sendRequest(url); // Se envía una solicitud HTTP a la URL especificada y se recibe la respuesta.
        switch (response.statusCode) {
            case HttpURLConnection.HTTP_OK: // Si la respuesta es 200 (OK), se procesa la URL.
                fileProcessor.processURL(url); // Se procesa la URL.
                break;
            case HttpURLConnection.HTTP_FORBIDDEN: // Si la respuesta es 403 (Forbidden), se muestra un mensaje de error de acceso denegado.
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 403</h1><h2>Status Message: Forbidden</h2><div>Error: Access denied for " + url + "</div></body></html>");
                break;
            case HttpURLConnection.HTTP_MOVED_PERM: // Si la respuesta es 301 (Moved Permanently) o 302 (Found), se redirige a la nueva URL y se descarga.
            case HttpURLConnection.HTTP_MOVED_TEMP:
                String newUrl = response.connection.getHeaderField("Location"); // Se obtiene la nueva URL de la cabecera "Location" de la respuesta.
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + response.statusCode + "</h1><h2>Status Message: Redirected</h2><div>Redirecting to " + newUrl + "</div></body></html>");
                download(newUrl); // Se sigue la redirección y se descarga el contenido de la nueva URL.
                break;
            case HttpURLConnection.HTTP_NOT_FOUND: // Si la respuesta es 404 (Not Found), se muestra un mensaje de error indicando que la URL no fue encontrada.
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 404</h1><h2>Status Message: Not Found</h2><div>Error: " + url + " not found</div></body></html>");
                break;
            default: // Si la respuesta no es ninguna de las anteriores, se muestra un mensaje de error indicando que no hay contenido disponible.
                System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + response.statusCode + "</h1><h2>Status Message: " + response.statusMessage + "</h2><div>Error: No content available</div></body></html>");
        }
        fetchHTML(url); // Se recupera el HTML de la URL, esto debe revisarse si solo debe recuperar directorios o manejar errores de manera diferente.
    }

    /**
     * Recupera el HTML de una URL y lo guarda en un archivo.
     *
     * @param url La URL del HTML a recuperar.
     */
    public void fetchHTML(String url) {
        // System.out.println("Fetching HTML for: " + url);
        HttpResponse response = socketManager.sendRequest(url); // Se envía una solicitud HTTP para recuperar el contenido HTML de la URL.
        if (response.statusCode == HttpURLConnection.HTTP_OK) { // Si la respuesta es 200 (OK), se guarda el contenido HTML en un archivo.
            fileManager.saveFile(response.data, url); // Se guarda el contenido HTML en un archivo.
            System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: 200</h1><h2>Status Message: OK</h2><div>HTML fetched successfully for " + url + "</div></body></html>");
        } else { // Si la respuesta no es 200 (OK), se muestra un mensaje de error indicando que no se pudo recuperar el HTML.
            System.out.println("<html><head><title>HTTP Response</title></head><body><h1>Status Code: " + response.statusCode + "</h1><h2>Status Message: " + response.statusMessage + "</h2><div>Failed to fetch HTML for: " + url + "</div></body></html>");
        }
    }
}
