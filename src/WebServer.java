import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {


    private ServerSocket ss; // listen for client connection requests on this server socket
    private final static LoggerService loggerService = new LoggerService(WebServer.class.getName());

    public WebServer(String staticFilePath, int port) {
        try {
            ss = new ServerSocket(port);
            loggerService.info("Server started, listening on port [" + port + "]");
            while (true) {
                // waits until client requests a connection, then returns connection (socket)
                Socket conn = ss.accept();
                loggerService.info("New connection found from [" + conn.getInetAddress() + "]");

                // create new handler for this connection
                RequestHandler ch = new RequestHandler(conn, staticFilePath);
                ch.start(); // start handler thread
            }
        } catch (IOException ioe) {
            loggerService.error("Error ", ioe);
        }
    }
}
