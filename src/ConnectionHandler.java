import enums.RequestMethod;
import enums.ResponseCode;
import exceptions.HttpException;
import exceptions.InternalServerErrorException;
import exceptions.RequestNotSupportedException;
import exceptions.ResourceNotFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class ConnectionHandler extends Thread {
    private BufferedReader br;
    private BufferedOutputStream bs;
    private final ResponseHandler responseHandler = new ResponseHandler();
    private final RequestHandler requestHandler = new RequestHandler();
    private final static LoggerService loggerService = new LoggerService(RequestHandler.class.getName());
    private final Path baseStaticFilePath;
    private final Socket conn;


    public ConnectionHandler(Socket conn, String staticFilePath) {
        super();
        this.conn = conn;
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // to read client data
            bs = new BufferedOutputStream(conn.getOutputStream());

        } catch (IOException ioe) {
            loggerService.error("", ioe);
        }
        // TODO there should be a check to make sure that the path for resolving static files is valid and it exists
        baseStaticFilePath = Paths.get("").toAbsolutePath().resolve(staticFilePath).normalize();
        loggerService.info("Static file directory at: " + baseStaticFilePath);
    }

    // run method is invoked when the Thread's start method (ch.start(); in Server class) is invoked
    public void run() {
        loggerService.info("Thread started for connection...");
        processRequest();

    }

    private void processRequest() {
        try {
            loggerService.info("======Request Received=====");
            ArrayList<String> requestStringLines = requestHandler.retrieveRequestString(br);
            // extract the headers and data [for post data mostly]
            Map<String, String> requestHeaders = requestHandler.extractRequestHeaders(requestStringLines);
            String data = requestHandler.extractRequestData();
            // now process the request using the response handler
            RequestMethod requestMethod = RequestMethod.valueOf(requestHeaders.getOrDefault(Headers.RequestMethod, "UNDEFINED").toUpperCase());
            loggerService.info("Processing [" + requestMethod + "] request");

            if (requestMethod == RequestMethod.GET) {
                processGetRequest(requestHeaders);
            } else if (requestMethod == RequestMethod.HEAD) {
                processHeadRequest(requestHeaders);
            } else if (requestMethod == RequestMethod.POST) {
                processPostRequest(requestHeaders, data);
            } else {
                throw new RequestNotSupportedException();
            }

        } catch (HttpException e) { // exit cleanly for any Exception (including IOException, ClientDisconnectedException)
            responseHandler.generateErrorResponse(bs, e);
        } catch (Exception e1) {
            responseHandler.generateErrorResponse(bs, new InternalServerErrorException(null, "Internal Server Error: " + e1.getMessage()));
        } finally {
            cleanup(); // cleanup and exit
        }
    }

    private void processHeadRequest(Map<String, String> headers) throws Exception {
        String relativeUrl = headers.getOrDefault(Headers.RelativeUrl, null);
        if (relativeUrl != null) {
            File file = requestHandler.findFile(baseStaticFilePath.toAbsolutePath().toString(), relativeUrl);
            if (file.exists() && requestHandler.isAllowedFileExtensions(file.getPath())) {
                responseHandler.generateResponse(bs, RequestMethod.HEAD, ResponseCode.Success, file);
            } else {
                throw new ResourceNotFoundException(RequestMethod.HEAD);
            }
        }
    }

    private void processGetRequest(Map<String, String> headers) throws Exception {
        String relativeUrl = headers.getOrDefault(Headers.RelativeUrl, null);
        if (relativeUrl != null) {
            File file = requestHandler.findFile(baseStaticFilePath.toAbsolutePath().toString(), relativeUrl);
            if (file != null && file.exists() && requestHandler.isAllowedFileExtensions(file.getPath())) {
                loggerService.info("Getting file [" + file.getPath() + "]");
                responseHandler.generateResponse(bs, RequestMethod.GET, ResponseCode.Success, file);// get headers
            } else {
                throw new ResourceNotFoundException(RequestMethod.GET);
            }
        }
    }

    public void processPostRequest(Map<String, String> headers, String data) {

    }

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