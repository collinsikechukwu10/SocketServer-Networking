import enums.RequestMethod;
import enums.ResponseCode;
import exceptions.HttpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public Map<String, String> generateResponseHeaders() {
        loggerService.info("Preparing headers for response");
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(Headers.Server, "Simple Java Http Server");
        return headers;

    }

    /**
     * Append headers related to the file.
     *
     * @param headers
     * @param file    file object
     */
    public void appendFileHeaders(Map<String, String> headers, File file) {
        if (file != null) {
            String mimeType = extractMimeType(file);
            if (mimeType != null) {
                headers.put(Headers.ContentType, mimeType);
            }
            headers.put(Headers.ContentSize, String.valueOf(file.length()));
        }
    }

    public String formatHeaders(ResponseCode responseCode, Map<String, String> headers) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(responseCode.getStatusResponse()).append(HEADER_SEPERATOR);
        headers.forEach((key, val) -> {
            responseBuilder.append(key + ": " + val + HEADER_SEPERATOR);
        });
        return responseBuilder.append(HEADER_SEPERATOR).toString();
    }

    public void generateResponse(OutputStream oi, RequestMethod requestMethod, ResponseCode responseCode, File file) throws IOException {
        Map<String, String> headers = generateResponseHeaders();
        if (file != null) {
            appendFileHeaders(headers, file);
        }
        // send headers to user
        byte[] headerBytes = formatHeaders(responseCode, headers).getBytes();
        oi.write(headerBytes, 0, headerBytes.length);
        oi.flush();
        if (requestMethod != RequestMethod.HEAD) {
            // send file if its exist
            FileInputStream fileInputStream = new FileInputStream(file);
            oi.write(fileInputStream.readAllBytes(), 0, (int) file.length());
            oi.flush();
        }

    }

    public void generateErrorResponse(OutputStream oi, HttpException ex) {
        try {
            loggerService.error("Error", ex);
            loggerService.info("Processing error...");
            Path errorFilePath = Paths.get("../Resources/www/error/", ex.getErrorCode() + ".html").toAbsolutePath().normalize();
            File file = new File(errorFilePath.toString());
            if (file.exists()) {
                loggerService.info("Generating error response...");
                Map<String, String> headers = generateResponseHeaders();
                appendFileHeaders(headers, file);
                byte[] headerBytes = formatHeaders(ResponseCode.resolveResponseCode(ex.getErrorCode()), headers).getBytes();
                oi.write(headerBytes, 0, headerBytes.length);
                oi.flush();
                if (ex.getRequestMethod() != RequestMethod.HEAD) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    int r;
                    while ((r = fileInputStream.read()) != -1) {
                        oi.write(r);
                    }
                }
            } else {
                loggerService.error("Error page does not exist [" + errorFilePath + "]");
            }
        } catch (IOException ioe) {
            loggerService.error("Error processing error page", ioe);
        }
    }
}
