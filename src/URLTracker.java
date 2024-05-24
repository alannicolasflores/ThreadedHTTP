import java.util.HashSet;
import java.util.Set;

public class URLTracker {
    private Set<String> visitedUrls = new HashSet<>();

    public boolean isVisited(String url) {
        // Retorna true si la URL ya ha sido visitada.
        return visitedUrls.contains(url);
    }

    public void markVisited(String url) {
        // Añade la URL al conjunto de URLs visitadas.
        visitedUrls.add(url);
    }

    public void resetTracker() {
        // Limpia el conjunto de URLs visitadas.
        visitedUrls.clear();
    }

    public int getNumberOfVisitedUrls() {
        // Retorna el número de URLs únicas que han sido visitadas.
        return visitedUrls.size();
    }
}
