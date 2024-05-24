public class ResponseFormatter {
    public static String formatHttpResponse(HttpResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>HTTP Response</title></head><body>");
        sb.append("<h1>Status Code: ").append(response.statusCode).append("</h1>");
        sb.append("<h2>Status Message: ").append(response.statusMessage).append("</h2>");
        if (response.data != null) {
            sb.append("<div>Content Loaded Successfully</div>");
        } else {
            sb.append("<div>Error: No content available</div>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }
}
