import java.io.*;
import java.net.URI;

public class FileManager {
    public void saveFile(InputStream data, String urlString) {
        try {
            URI uri = new URI(urlString.replace(" ", "%20")); // Handle spaces correctly
            String path = uri.getPath().substring(1); // Remove the leading '/' from path
            path = path.replace('/', File.separatorChar); // Replace URL separators with system file separators
            File file = new File("downloaded" + File.separator + path); // Save under 'downloaded' directory
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs(); // Create the directory if it doesn't exist
            }
            try (FileOutputStream out = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = data.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            ///System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
