import exceptions.HttpException;
import exceptions.InternalServerErrorException;
import exceptions.RequestNotSupportedException;
import exceptions.ResourceNotFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler extends Thread {
    //    private InputStream is; // get data from client on this input stream
//    private OutputStream os; // can send data back to the client on this output stream
    private Socket conn;
    private BufferedReader br;
    private PrintWriter bw;
    private final ResponseHandler responseHandler;
    private final static String PROTOCOL_METHOD = "HTTP/1.1";
    private final static List<String> ALLOWED_EXTENSIONS = List.of("png", "jpg", "pdf", "html", "csv");
    private final static LoggerService loggerService = new LoggerService(RequestHandler.class.getName());
    private final Path baseStaticFilePath;


    public RequestHandler(Socket conn, String staticFilePath) throws IOException {
        this.conn = conn;
        responseHandler = new ResponseHandler();
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // to read client data
            bw = new PrintWriter(conn.getOutputStream(), true);

        } catch (IOException ioe) {
            loggerService.error("", ioe);
        }

        baseStaticFilePath = Paths.get("").toAbsolutePath().resolve(staticFilePath).normalize();
        loggerService.info("Static file directory at: " + baseStaticFilePath);

    }

    // run method is invoked when the Thread's start method (ch.start(); in Server class) is invoked
    public void run() {
        loggerService.info("Thread started for connection...");
        try {
            processRequest();
        } catch (HttpException e) { // exit cleanly for any Exception (including IOException, ClientDisconnectedException)
            processErrorMessage(e);
        } catch (Exception e1) {
            processErrorMessage(new InternalServerErrorException("Internal Server Error"));
        } finally {
            cleanup(); // cleanup and exit
        }
    }

    private void processRequest() throws Exception {
        // process headers
        Map<String, String> headers = processHeaders();
        // use the request type to process requests
        String requestMethod = headers.getOrDefault(Headers.RequestMethod, null);
        switch (requestMethod) {
            case "GET":
                loggerService.info("Processing get request");
                processGetRequest(headers);
                break;
            case "POST":
                processPostRequest(headers);
                break;
            case "HEAD":
                loggerService.info("Processing head request");
                processHeadRequest(headers);
                break;
            default:
                throw new RequestNotSupportedException("");
        }

    }

    private void processHeadRequest(Map<String, String> headers) throws Exception {
        String relativeUrl = headers.getOrDefault(Headers.RelativeUrl, null);
        if (relativeUrl != null) {
            File file = resolveFilePath(relativeUrl).toFile();
            if (file.exists() && isAllowedFileExtensions(file.getPath())) {
                // get headers
                String responseHeaders = responseHandler.generateAppropriateHeaders(ResponseCode.Success, file);
                // send data to client and then close the connection
                loggerService.info("======Response Sent=====");
                loggerService.info(responseHeaders);
                bw.print(responseHeaders);
            } else {
                throw new ResourceNotFoundException("");
            }
        }
    }

    private void processGetRequest(Map<String, String> headers) throws Exception {
        String relativeUrl = headers.getOrDefault(Headers.RelativeUrl, null);
        if (relativeUrl != null) {
            File file = resolveFilePath(relativeUrl).toFile();
            if (file.exists() && isAllowedFileExtensions(file.getPath())) {
                loggerService.info("Getting file [" + file.getPath() + "]");
                String responseHeaders = responseHandler.generateAppropriateHeaders(ResponseCode.Success, file);
                FileInputStream fileInputStream = new FileInputStream(file);
                int r;
                loggerService.info("======Response Sent=====");
                loggerService.info(responseHeaders);
                bw.println(responseHeaders);
                while ((r = fileInputStream.read()) != -1) {
                    bw.write(r);
                }
            } else {
                throw new ResourceNotFoundException("");
            }
        }
    }

    private void processPostRequest(Map<String, String> headers) {

    }

    public void processErrorMessage(HttpException ex) {
        try {
            loggerService.error("Error", ex);
            loggerService.info("Processing error...");
            Path errorFilePath = Paths.get("../Resources/www/error/", ex.getErrorCode() + ".html").toAbsolutePath().normalize();
            File file = new File(errorFilePath.toString());
            if (file.exists() && isAllowedFileExtensions(file.getPath())) {
                loggerService.info("Generating response...");
                loggerService.info(file.getPath());
                String responseHeaders = responseHandler.generateAppropriateHeaders(ResponseCode.resolveResponseCode(ex.getErrorCode()), file);
                FileInputStream fileInputStream = new FileInputStream(file);
                int r;
//                os.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
                bw.write(responseHeaders.toCharArray());
                bw.flush();
                while ((r = fileInputStream.read()) != -1) {
                    bw.write(r);
                }
            } else {
                loggerService.error("Error page does not exist [" + errorFilePath + "]");
            }
        } catch (IOException ioe) {
            loggerService.error("Error processing error page", ioe);
        }
    }

    private boolean isAllowedFileExtensions(String filePath) {
        // this should check if the fille extension is right or not
        String[] splits = filePath.split("\\.");
        loggerService.info(splits[splits.length - 1]);
        return ALLOWED_EXTENSIONS.contains(splits[splits.length - 1].toLowerCase());
    }

    private Map<String, String> processHeaders() throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        loggerService.info("====Request Received======");
        do {
            line = br.readLine();
            loggerService.info(line);
            // check for request method and url
            if (line.contains(PROTOCOL_METHOD)) {
                String[] data = line.replace(PROTOCOL_METHOD, "").split("/");
                if (data.length == 2) {
                    headers.put(Headers.RequestMethod, data[0].trim());
                    headers.put(Headers.RelativeUrl, data[1].trim());
                }
            }
        } while (!line.isEmpty());
        return headers;
    }

    private void cleanup() {
        loggerService.info("Closing connection...");
        try {
            br.close();
            bw.close();
            conn.close();
        } catch (IOException ioe) {
            loggerService.error("Error closing connection", ioe);
        }
    }

    private Path resolveFilePath(String relativeUrl) {
        return Paths.get(baseStaticFilePath.toString(), relativeUrl);
    }
}
