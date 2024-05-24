public class URLValidator {
    /**
     * Validates that a given string is a well-formed URL and URI.
     *
     * @param url The string to validate.
     * @return true if the URL is well-formed, false otherwise.
     */
    public boolean validate(String url) {
        try {
            // Intenta crear un objeto URL y convertirlo a URI
            new java.net.URL(url).toURI();
            return true;
        } catch (java.net.MalformedURLException e) {
            // La URL no está en formato válido
            System.out.println("Invalid URL format: " + e.getMessage());
            return false;
        } catch (java.net.URISyntaxException e) {
            // La URL no cumple con la sintaxis de URI
            System.out.println("Invalid URI syntax: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada
            System.out.println("An error occurred during URL validation: " + e.getMessage());
            return false;
        }
    }
}
