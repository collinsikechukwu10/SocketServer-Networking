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

/**
 * Response Handler class.
 * Contains utility functions to process and handling responses to clients.
 *
 * @author 210032207
 */
public class ResponseHandler {
    private static final String HEADER_SEPERATOR = "\r\n";
    private final LoggerService loggerService = LoggerService.getInstance();
    private static final String ERROR_FILES_PATH = "../www/error/";

    /**
     * Default constructor.
     */
    public ResponseHandler() {
    }

    /**
     * Deduce the mimetype of a file using its file extension.
     *
     * @param file file object to check
     * @return mimetype of the file
     */
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
                    // we send a default plain mimetype
                    return "text/plain";
            }
        }
        return null;
    }

    /**
     * Append all response headers (including those related to a requested resource like files).
     * For files, it adds the size and the mime type (if it exists).
     *
     * @param file file object to generate headers for
     * @return response headers
     */
    public Map<String, String> generateResponseHeaders(File file) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(Headers.SERVER, "Simple Java Http Server");
        if (file != null) {
            String mimeType = extractMimeType(file);
            if (mimeType != null) {
                headers.put(Headers.CONTENT_TYPE, mimeType);
            }
            headers.put(Headers.CONTENT_SIZE, String.valueOf(file.length()));
        }
        return headers;

    }


    /**
     * Converts headers into a http format.
     *
     * @param responseCode response code enum for the response
     * @param headers      headers to convert
     * @return response headers string
     */
    public String formatHeaders(ResponseCode responseCode, Map<String, String> headers) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(responseCode.getStatusResponse()).append(HEADER_SEPERATOR);
        // convert header map to its string format
        headers.forEach((key, val) -> {
            responseBuilder.append(key).append(": ").append(val).append(HEADER_SEPERATOR);
        });
        return responseBuilder.append(HEADER_SEPERATOR).toString();
    }

    /**
     * Generates response for implemented http methods.
     *
     * @param oi            output stream to write response to
     * @param requestMethod request method of the request
     * @param responseCode  response code enum to respond with
     * @param file          file object for situations where a file is being sent back to the client
     * @throws IOException if error is encountered during writes to output stream
     */
    public void generateResponse(OutputStream oi, RequestMethod requestMethod, ResponseCode responseCode, File file) throws IOException {
        Map<String, String> headers = generateResponseHeaders(file);
        // send headers to user
        byte[] headerBytes = formatHeaders(responseCode, headers).getBytes();
        oi.write(headerBytes);
        if (requestMethod != RequestMethod.HEAD && file != null) {
            // send file if its exist and request is not a head request
            FileInputStream fileInputStream = new FileInputStream(file);
            oi.write(fileInputStream.readAllBytes());
        }
        oi.flush();
        loggerService.info("Response Code: [" + responseCode.getCode() + "]");
    }

    /**
     * Generates response for implemented http errors and exceptions.
     *
     * @param oi output stream to write response to
     * @param ex exception to create error response from
     */
    public void generateErrorResponse(OutputStream oi, HttpException ex) {
        try {
            loggerService.error("Error", ex);
            Path errorFilePath = Paths.get(ERROR_FILES_PATH, ex.getErrorCode() + ".html").toAbsolutePath().normalize();
            File file = new File(errorFilePath.toString());
            if (file.exists()) {
                Map<String, String> headers = generateResponseHeaders(file);
                ResponseCode responseCode = ResponseCode.resolveResponseCode(ex.getErrorCode());
                byte[] headerBytes = formatHeaders(responseCode, headers).getBytes();
                oi.write(headerBytes, 0, headerBytes.length);
                if (ex.getRequestMethod() != RequestMethod.HEAD) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    oi.write(fileInputStream.readAllBytes(), 0, (int) file.length());
                    loggerService.info("Response Code: [" + responseCode.getCode() + "]");
                }
                oi.flush();
            } else {
                loggerService.error("Error page does not exist [" + errorFilePath + "]");
            }
        } catch (IOException ioe) {
            loggerService.error("Error processing error page", ioe);
        }
    }
}
