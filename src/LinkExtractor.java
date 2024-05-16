import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class LinkExtractor {
    public static List<String> extractLinks(String baseUrl, String html) {
        List<String> links = new ArrayList<>();
        Matcher matcher = Pattern.compile("<a\\s+href=\"([^\"]+)\">([^<]+)</a>", Pattern.CASE_INSENSITIVE).matcher(html);
        while (matcher.find()) {
            String url = matcher.group(1);
            String linkText = matcher.group(2).trim();

            // Omitir enlaces a directorios padre
            if (linkText.equalsIgnoreCase("Parent Directory")) {
                continue;
            }

            // Construir URL completa
            String fullUrl;
            if (url.startsWith("http")) {
                fullUrl = url;
            } else {
                if (baseUrl.endsWith("/")) {
                    fullUrl = baseUrl + url;
                } else {
                    fullUrl = baseUrl + "/" + url;
                }
            }

            // Filtrar enlaces para incluir solo aquellos que sean archivos válidos o directorios que terminan en "/"
            if (url.matches(".*\\.(java|txt|zip|pdf|doc|docx|ppt|pptx|xls|xlsx|png|jpg|jpeg|gif|mp3|mp4|avi|mkv)") || url.endsWith("/")) {
                // Imprimir URL completa para depuración
                System.out.println("Processed URL: " + fullUrl);

                // Agregar URL a la lista de enlaces
                links.add(fullUrl);
            }
        }
        return links;
    }
}
