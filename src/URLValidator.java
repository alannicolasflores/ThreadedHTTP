public class URLValidator {
    /**
     * Validates that a given string is a well-formed URL and URI.
     *
     * @param url The string to validate.
     * @return true if the URL is well-formed, false otherwise.
     */
    public boolean validate(String url) {
        try {
            // Intenta crear un objeto URL y convertirlo a URI para verificar su validez
            new java.net.URL(url).toURI();
            return true;  // Retorna verdadero si la URL es válida
        } catch (java.net.MalformedURLException e) {
            // Captura errores específicos de formato incorrecto de URL
            System.out.println("Invalid URL format: " + e.getMessage());
            return false;  // Retorna falso si la URL tiene un formato incorrecto
        } catch (java.net.URISyntaxException e) {
            // Captura errores de sintaxis URI, que son más estrictos que simplemente formatos de URL
            System.out.println("Invalid URI syntax: " + e.getMessage());
            return false;  // Retorna falso si la sintaxis URI no es válida
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada que podría ocurrir durante la validación
            System.out.println("An error occurred during URL validation: " + e.getMessage());
            return false;  // Retorna falso si ocurre un error no específico
        }
    }
}
