import java.io.*;
import java.net.*;

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

        while ((line = reader.readLine()) != null) {
            response.append(line).append("\r\n");

            if (!headersEnded && line.isEmpty()) {
                headersEnded = true;
                String headers = response.toString();
                String[] headerLines = headers.split("\r\n");
                for (String headerLine : headerLines) {
                    if (headerLine.toLowerCase().startsWith("content-length:")) {
                        contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                        break;
                    }
                }
                break; // Stop reading headers
            }
        }

        if (contentLength != -1) {
            char[] buffer = new char[contentLength];
            int bytesRead = reader.read(buffer, 0, contentLength);
            response.append(buffer, 0, bytesRead);
        } else {
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\r\n");
            }
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
