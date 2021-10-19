import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHandler {
    private final static String HEADER_SEPERATOR = "\r\n";
    private final static LoggerService loggerService = new LoggerService(ResponseHandler.class.getName());


    public String extractMimeType(File file) {
        // get file extension
        String[] splits = file.getPath().split("\\.");
        String extension = splits[splits.length - 1];
        if (extension != null) {
            switch (extension.toLowerCase()) {
                case "html":
                    return "text/html";
                case "png":
                case "jpeg":
                case "jpg":
                case "gif":
                    return "image/" + extension.toLowerCase();
                default:
                    loggerService.error("Could not retrieve mimetype for the file requested");
                    return null;
            }
        }
        return null;
    }


    public String generateAppropriateHeaders(ResponseCode responseCode, File file) {
        // generate headers map, ALSO i want to assume that you need the map ordered
        loggerService.info("Preparing headers for response");
        StringBuilder responseBuilder = new StringBuilder();
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(Headers.Server, "Simple Java Http Server");
        if (file != null) {
            String mimeType = extractMimeType(file);
            if (mimeType != null) {
                headers.put(Headers.ContentType, mimeType);
            }
            headers.put(Headers.ContentSize, String.valueOf(file.length()));
        }

        // add the response and then put the other headers
        responseBuilder.append(responseCode.getStatusResponse()).append(HEADER_SEPERATOR);
        headers.forEach((key, val) -> {
            String headerValue = key + ": " + val + HEADER_SEPERATOR;
            responseBuilder.append(headerValue);
        });
        return responseBuilder.toString();
    }
}
