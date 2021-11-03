import enums.ResponseCode;
import exceptions.HttpException;
import exceptions.RequestNotSupportedException;
import exceptions.ResourceNotFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Request Handler class
 *
 * @author 210032207
 */
public class RequestHandler extends Thread {
    //    private InputStream is; // get data from client on this input stream
//    private OutputStream os; // can send data back to the client on this output stream
    private final static String PROTOCOL_METHOD = "HTTP/1.1";
    private final static List<String> ALLOWED_EXTENSIONS = List.of("png", "jpg", "pdf", "html", "csv");
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
     * @return
     */
    public boolean isAllowedFileExtensions(String filePath) {
        // this should check if the fille extension is right or not
        String[] splits = filePath.split("\\.");
        loggerService.info(splits[splits.length - 1]);
        return ALLOWED_EXTENSIONS.contains(splits[splits.length - 1].toLowerCase());
    }

    /**
     * Extract request headeras from an incoming request.
     *
     * @param requestStringLines list of lines in an incoming request
     * @return list of request headers
     */
    public Map<String, String> extractRequestHeaders(ArrayList<String> requestStringLines) {
        Map<String, String> headers = new HashMap<>();
        loggerService.info("====Request Received======");
        requestStringLines.forEach(line -> {
            if (line.contains(PROTOCOL_METHOD)) {
                String[] data = line.replace(PROTOCOL_METHOD, "").split("/");
                if (data.length == 2) {
                    headers.put(Headers.RequestMethod, data[0].trim());
                    headers.put(Headers.RelativeUrl, data[1].trim());
                }
            }
        });
        return headers;
    }

    /**
     * Gets lines of the incoming request from the sockets input stream
     *
     * @param br
     * @return list of lines from incoming request
     * @throws IOException
     */
    public ArrayList<String> retrieveRequestString(BufferedReader br) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String line;
        do {
            line = br.readLine();
            lines.add(line);
            loggerService.info(line);
        } while (!line.isEmpty());
        return lines;
    }

    /**
     * Extract request data fom request
     * @return
     * @throws Exception
     */
    public String extractRequestData() throws Exception {
        return "";
    }

    /**
     * Finds a file based on the base and relative path
     *
     * @param basePath
     * @param relativePath
     * @return
     */
    public File findFile(String basePath, String relativePath) {
        return Paths.get(basePath, relativePath).toFile();
    }
}
