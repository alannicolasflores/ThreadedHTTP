import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class SocketManager implements Closeable {
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private InputStream inputStream;

    public SocketManager(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.inputStream = socket.getInputStream();
    }

    public void sendGetRequest(String file, String host) throws IOException {
        writer.write("GET " + file + " HTTP/1.1\r\n");
        writer.write("Host: " + host + "\r\n");
        writer.write("Connection: close\r\n");
        writer.write("\r\n");
        writer.flush();
    }

    public String readResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        boolean headersEnded = false;
        int contentLength = -1;
        String contentType = null;

        // Leer los encabezados de la respuesta
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\r\n");

            if (!headersEnded && line.isEmpty()) {
                headersEnded = true;
                String headers = response.toString();
                String[] headerLines = headers.split("\r\n");
                for (String headerLine : headerLines) {
                    if (headerLine.toLowerCase().startsWith("content-length:")) {
                        contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                    } else if (headerLine.toLowerCase().startsWith("content-type:")) {
                        contentType = headerLine.split(":")[1].trim();
                    }
                }
                break; // Dejar de leer los encabezados
            }
        }

        // Leer el cuerpo de la respuesta
        if (contentLength != -1) {
            char[] buffer = new char[contentLength];
            int bytesRead = reader.read(buffer, 0, contentLength);
            response.append(buffer, 0, bytesRead);
        } else {
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\r\n");
            }
        }

        // Decodificar el contenido utilizando la codificación especificada en Content-Type
        if (contentType != null) {
            String encoding = "UTF-8"; // Establecer una codificación predeterminada
            String[] contentTypeParts = contentType.split(";");
            for (String part : contentTypeParts) {
                if (part.trim().startsWith("charset=")) {
                    encoding = part.trim().substring(8);
                    break;
                }
            }
            return new String(response.toString().getBytes(encoding), StandardCharsets.UTF_8);
        }

        return response.toString();
    }

    @Override
    public void close() throws IOException {
        if (reader != null) reader.close();
        if (writer != null) writer.close();
        if (socket != null) socket.close();
    }
}
