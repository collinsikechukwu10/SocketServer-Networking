import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {


    private ServerSocket ss; // listen for client connection requests on this server socket

    public WebServer(String staticFilePath, int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started ... listening on port " + port + " ...");
            while (true) {
                // waits until client requests a connection, then returns connection (socket)
                Socket conn = ss.accept();
                System.out.println("Server got new connection request from " + conn.getInetAddress());

                // create new handler for this connection
                RequestHandler ch = new RequestHandler(conn);
                ch.start(); // start handler thread
            }
        } catch (IOException ioe) {
            System.out.println("Ooops " + ioe.getMessage());
        }
    }
}
