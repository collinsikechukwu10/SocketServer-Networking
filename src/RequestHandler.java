import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Request Handler class.
 *
 * @author 210032207
 */
public class RequestHandler extends Thread {
    private static final String PROTOCOL_METHOD = "HTTP/1.1";
    private static final List<String> ALLOWED_EXTENSIONS = List.of("png", "jpg", "pdf", "html", "csv");
    private final LoggerService loggerService = LoggerService.getInstance();

    /**
     * Default constructor.
     */
    public RequestHandler() {
    }

    /**
     * Checks whether the file type is allowed.
     *
     * @param filePath file path to check for extension
     * @return if file extension is allowed
     */
    public boolean isAllowedFileExtensions(String filePath) {
        // this should check if the fille extension is right or not
        String[] splits = filePath.split("\\.");
        return ALLOWED_EXTENSIONS.contains(splits[splits.length - 1].toLowerCase());
    }

    /**
     * Extract request headers from an incoming request.
     *
     * @param requestStringLines list of lines in an incoming request
     * @return list of request headers
     */
    public Map<String, String> extractRequestHeaders(ArrayList<String> requestStringLines) {
        Map<String, String> headers = new HashMap<>();
        requestStringLines.forEach(line -> {
            if (line.contains(PROTOCOL_METHOD)) {
                StringTokenizer data = new StringTokenizer(line.replace(PROTOCOL_METHOD, ""), "/");

                if (data.countTokens() > 0) {
                    headers.put(Headers.REQUEST_METHOD, data.nextToken().trim());
                    StringBuilder relativeUrl = new StringBuilder();
                    while (data.hasMoreTokens()) {
                        relativeUrl.append("/").append(data.nextToken().trim());
                    }
                    headers.put(Headers.RELATIVE_URL, relativeUrl.toString());
                    loggerService.info("Request made to relative Url: [" + relativeUrl + "]");
                }
            } else if (line.contains(Headers.CONTENT_TYPE)) {
                String[] contentType = line.split(":");
                headers.put(Headers.CONTENT_TYPE, contentType[contentType.length - 1].trim());
            } else if (line.contains(Headers.CONTENT_SIZE)) {
                String[] contentSize = line.split(":");
                headers.put(Headers.CONTENT_SIZE, contentSize[contentSize.length - 1].trim());
            } else if (line.contains(Headers.ACCEPT)) {
                String[] acceptedTypes = line.split(":");
                headers.put(Headers.ACCEPT, acceptedTypes[acceptedTypes.length - 1].trim());
            }
        });
        loggerService.info(headers.toString());
        return headers;
    }

    /**
     * Gets lines of the incoming request from the sockets input stream.
     *
     * @param br buffered reader
     * @return list of lines from incoming request
     * @throws IOException if error occurs while reading line from input stream
     */
    public ArrayList<String> retrieveRequestString(BufferedReader br) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String line;
        do {
            line = br.readLine();
            lines.add(line);
        } while (!line.isEmpty());
        return lines;
    }

    /**
     * Extract request data fom request.
     *
     * @param br buffered reader
     * @return nothing at the moment
     * @throws IOException if error occurs while reading data
     */
    public String extractRequestData(BufferedReader br) throws IOException {
        //code to read the post payload data
        StringBuilder payload = new StringBuilder();
        while (br.ready()) {
            payload.append((char) br.read());
        }
        return payload.toString();
    }

    /**
     * Finds a file based on the base and relative path.
     *
     * @param basePath     base path
     * @param relativePath relative path
     * @return file object representing filepath
     */
    public File findFile(String basePath, String relativePath) {
        return Paths.get(basePath, relativePath).toFile();
    }
}
