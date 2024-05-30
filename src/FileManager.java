// Importa la clase InputStream del paquete java.io para manejar flujos de entrada de datos.
import java.io.*;
// Importa la clase URI del paquete java.net para manejar identificadores de recursos uniformes.
import java.net.URI;

// Define la clase FileManager que se encarga de gestionar la descarga y almacenamiento de archivos.
public class FileManager {
    // Método saveFile que guarda un archivo a partir de un flujo de entrada de datos y una URL.
    public void saveFile(InputStream data, String urlString) {
        try {
            // Crea un objeto URI a partir de la URL dada, reemplazando los espacios por "%20" para manejarlos correctamente.
            URI uri = new URI(urlString.replace(" ", "%20"));
            // Obtiene el path del URI y elimina el '/' inicial.
            String path = uri.getPath().substring(1);
            // Reemplaza los separadores de la URL por los separadores de archivos del sistema.
            path = path.replace('/', File.separatorChar);
            // Crea un objeto File para representar el archivo a guardar bajo el directorio 'downloaded'.
            File file = new File("downloaded" + File.separator + path);
            // Obtiene el directorio padre del archivo.
            File parentDir = file.getParentFile();
            // Verifica si el directorio padre no existe y lo crea si es necesario.
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            // Utiliza un bloque try-with-resources para garantizar el cierre automático del flujo de salida.
            try (FileOutputStream out = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                // Lee datos del flujo de entrada y los escribe en el archivo hasta que no haya más datos disponibles.
                while ((bytesRead = data.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            // Maneja cualquier excepción y la imprime en caso de error.
            // System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
