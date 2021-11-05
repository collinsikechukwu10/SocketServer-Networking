import enums.RequestMethod;
import enums.ResponseCode;
import exceptions.*;

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


/**
 * Connection Handler class.
 *
 * @author 210032007
 */
public class ConnectionHandler extends Thread {
    private BufferedReader br;
    private BufferedOutputStream bs;
    private final ResponseHandler responseHandler = new ResponseHandler();
    private final RequestHandler requestHandler = new RequestHandler();
    private final LoggerService loggerService = LoggerService.getInstance();
    private final Path baseStaticFilePath;
    private final Socket conn;

    /**
     * Constructor specifying socket connection and file path used to serve the static pages.
     *
     * @param conn           socket connection
     * @param staticFilePath file path serving the static pages
     */
    public ConnectionHandler(Socket conn, String staticFilePath) {
        super();
        this.conn = conn;
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // to read client data
            bs = new BufferedOutputStream(conn.getOutputStream());
        } catch (IOException ioe) {
            loggerService.error("", ioe);
        }
        baseStaticFilePath = Paths.get("").toAbsolutePath().resolve(staticFilePath).normalize();
    }

    /**
     * Run method invoked the Thread's start method (ch.start(); in WebServer class) is invoked.
     */
    @Override
    public void run() {
        processRequest();

    }

    /**
     * This processes the request by extracting the headers and data, then sends the response data.
     */
    private void processRequest() {
        try {
            loggerService.info("Request Received: [" + LocalDateTime.now() + "]");
            ArrayList<String> requestStringLines = requestHandler.retrieveRequestString(br);
            // extract the headers and data [for post data mostly]
            Map<String, String> requestHeaders = requestHandler.extractRequestHeaders(requestStringLines);
            // now process the request using the response handler
            RequestMethod requestMethod = RequestMethod.resolveRequestMethod(requestHeaders.getOrDefault(Headers.REQUEST_METHOD, "UNDEFINED"));
            loggerService.info("Request Type: [" + requestMethod + "]");

            if (requestMethod == RequestMethod.GET) {
                processGetRequest(requestHeaders);
            } else if (requestMethod == RequestMethod.HEAD) {
                processHeadRequest(requestHeaders);
            } else if (requestMethod == RequestMethod.POST) {
                processPostRequest(requestHeaders);
            } else if (requestMethod == RequestMethod.DELETE) {
                processDeleteRequest(requestHeaders);
            } else {
                throw new RequestNotSupportedException();
            }
        } catch (HttpException e) {
            responseHandler.generateErrorResponse(bs, e);
        } catch (Exception e1) {
            responseHandler.generateErrorResponse(bs, new InternalServerErrorException(null, "Internal Server Error: " + e1.getMessage()));
        } finally {
            // Here we always cleanup and close the connection to prevent any kept-alive connections.
            cleanup(); // cleanup and exit
        }
    }

    /**
     * Processes a HEAD request.
     *
     * @param headers extracted request headers
     * @throws HttpException any http exception
     * @throws IOException   occurs if streaming issues exists
     */
    private void processHeadRequest(Map<String, String> headers) throws HttpException, IOException {
        String relativeUrl = headers.getOrDefault(Headers.RELATIVE_URL, null);
        if (relativeUrl != null) {
            File file = requestHandler.findFile(baseStaticFilePath.toAbsolutePath().toString(), relativeUrl);
            if (file.exists() && requestHandler.isAllowedFileExtensions(file.getPath())) {
                responseHandler.generateResponse(bs, RequestMethod.HEAD, ResponseCode.Success, file);
                return;
            }
        }
        throw new ResourceNotFoundException(RequestMethod.HEAD);
    }

    /**
     * Processes a GET request.
     *
     * @param headers extracted request headers
     * @throws HttpException any http exception
     * @throws IOException   occurs if streaming issues exists
     */
    private void processGetRequest(Map<String, String> headers) throws HttpException, IOException {
        String relativeUrl = headers.getOrDefault(Headers.RELATIVE_URL, null);
        if (relativeUrl != null) {
            File file = requestHandler.findFile(baseStaticFilePath.toAbsolutePath().toString(), relativeUrl);
            if (file != null && file.exists() && requestHandler.isAllowedFileExtensions(file.getPath())) {
                loggerService.info("Getting file [" + file.getPath() + "]");
                responseHandler.generateResponse(bs, RequestMethod.GET, ResponseCode.Success, file);
                return;
            }
        }
        throw new ResourceNotFoundException(RequestMethod.GET);
    }

    /**
     * Processes a POST request.
     * This only works for post request that have the `application/x-www-form-urlencoded` content type.
     *
     * @param headers extracted request headers
     * @throws HttpException any http exception
     * @throws IOException   occurs if streaming issues exists
     */
    public void processPostRequest(Map<String, String> headers) throws HttpException, IOException {
        // use content type to ascertain how you process the data
        if (Objects.equals(headers.get(Headers.CONTENT_TYPE), "application/x-www-form-urlencoded")) {
            String data = requestHandler.extractRequestData(br);
            loggerService.info("===Post Request Data===");
            loggerService.info(data);
            loggerService.info("=======================");
            responseHandler.generateResponse(bs, RequestMethod.POST, ResponseCode.Success, null);
        } else {
            throw new BadRequestException(RequestMethod.POST);
        }
    }

    /**
     * Processes a DELETE request.
     *
     * @param headers extracted request headers
     * @throws HttpException any http exception
     * @throws IOException   occurs if streaming issues exists
     */
    public void processDeleteRequest(Map<String, String> headers) throws HttpException, IOException {
        String relativeUrl = headers.getOrDefault(Headers.RELATIVE_URL, null);
        if (relativeUrl != null) {
            File file = requestHandler.findFile(baseStaticFilePath.toAbsolutePath().toString(), relativeUrl);
            if (file != null && file.exists()) {
                if (file.delete()) {
                    // since the response message does not include any representation like a html page
                    // describing the status, we respond with a 204 staus code instead of the normal 200
                    // [https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods/DELETE]
                    responseHandler.generateResponse(bs, RequestMethod.DELETE, ResponseCode.SuccessNoContent, null);
                    return;
                } else {
                    throw new InternalServerErrorException(RequestMethod.DELETE, "Error deleting file requested");
                }
            }
        }
        throw new ResourceNotFoundException(RequestMethod.DELETE);
    }

    /**
     * Cleans up the request and closes the connections.
     */
    private void cleanup() {
        loggerService.info("Closing connection...");
        try {
            br.close();
            bs.close();
            conn.close();
        } catch (IOException ioe) {
            loggerService.error("Error closing connection", ioe);
        }
    }
}
