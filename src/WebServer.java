import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Web Server Class.
 * This class creates the server socket and wait for connections.
 *
 * @author 210032207
 */
public class WebServer {


    private ServerSocket ss; // listen for client connection requests on this server socket
    private static final LoggerService LOG = LoggerService.getInstance();

    /**
     * Constructor specifying file path to serve static pages, and port number for the server.
     *
     * @param staticFilePath file path to find static pages
     * @param port           port number for server socket
     */
    public WebServer(String staticFilePath, int port) {
        try {
            ss = new ServerSocket(port);
            LOG.info("Server started, listening on port [" + port + "]");
            while (true) {

                // waits until client requests a connection, then returns connection (socket)
                Socket conn = ss.accept();
                LOG.info("New connection found from [" + conn.getInetAddress() + "], Starting thread...");

                // create new handler for this connection
                ConnectionHandler ch = new ConnectionHandler(conn, staticFilePath);

                // start handler thread
                ch.start();
            }
        } catch (IOException ioe) {
            LOG.error("Error ", ioe);
        }
    }
}
