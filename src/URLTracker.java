// Importa la clase HashSet del paquete java.util para utilizar una estructura de conjunto sin duplicados.
import java.util.HashSet;
// Importa la clase Set del paquete java.util para utilizar una interfaz de conjunto.
import java.util.Set;

// Define la clase URLTracker que realiza un seguimiento de las URL visitadas.
public class URLTracker {
    // Conjunto para almacenar las URL visitadas sin duplicados.
    private Set<String> visitedUrls = new HashSet<>();

    // Método para verificar si una URL ya ha sido visitada.
    public boolean isVisited(String url) {
        // Retorna true si la URL ya ha sido visitada.
        return visitedUrls.contains(url);
    }

    // Método para marcar una URL como visitada.
    public void markVisited(String url) {
        // Añade la URL al conjunto de URLs visitadas.
        visitedUrls.add(url);
    }

    // Método para reiniciar el seguimiento de las URL visitadas.
    public void resetTracker() {
        // Limpia el conjunto de URLs visitadas.
        visitedUrls.clear();
    }

    // Método para obtener el número de URLs únicas que han sido visitadas.
    public int getNumberOfVisitedUrls() {
        // Retorna el tamaño del conjunto de URLs visitadas.
        return visitedUrls.size();
    }
}
