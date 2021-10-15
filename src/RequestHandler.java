import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler extends Thread{
    private InputStream is; // get data from client on this input stream
    private OutputStream os; // can send data back to the client on this output stream
    private Socket conn;
    private BufferedReader br;

    interface Headers{
        String REQUEST_METHOD = "RequestMethod";
        String RELATIVE_URL = "RelativeUrl";
    }

    public RequestHandler(Socket conn) {
        this.conn = conn;
        try {
            is = conn.getInputStream(); // get data from client on this input stream
            os = conn.getOutputStream(); // to send data back to the client on this stream
            br = new BufferedReader(new InputStreamReader(is)); // to read client data
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler: " + ioe.getMessage());
        }
    }

    // run method is invoked when the Thread's start method (ch.start(); in Server class) is invoked
    public void run() {
        System.out.println("new ConnectionHandler thread started .... ");
        try {
            printClientData();
        } catch (Exception e) {
            // exit cleanly for any Exception (including IOException, ClientDisconnectedException)
            System.out.println("ConnectionHandler:run " + e.getMessage());
            cleanup(); // cleanup and exit
        }
    }



    private void printClientData() throws  Exception {
        while (true) {
            String line = br.readLine(); // get data from client over socket

            // if readLine fails we can deduce here that the connection to the client is broken
            // and shut down the connection on this side cleanly by throwing a DisconnectedException
            // which will be passed up the call stack to the nearest handler (catch block)
            // in the run method
            if (line == null || line.equals("null") || line.equals("end")) {
                throw new Exception(" ... client has closed the connection ... ");
            }

            // in this simple setup all the server does in response to messages from the client is
            // to send a single ACK byte back to client - the client uses this ACK byte to test
            // whether the connection to this server is still live, if not the client shuts down cleanly
            os.write(1); //Configuration.ackByte

            // assuming no exception, print out line received from client
            System.out.println("ConnectionHandler: " + line);
        }
    }

    private Map<String,String> processHeaders() throws IOException {
        Map<String,String> headers = new HashMap<>();
        String line="";
        do{
            line = br.readLine();
            // check for request method and url
            if(line.contains("HTTP/1.1")){
                String[] data = line.replace("HTTP/1.1","").split("/");
                if (data.length == 2){
                    headers.put(Headers.REQUEST_METHOD, data[0]);
                    headers.put(Headers.RELATIVE_URL, data[1]);
                }
            }
        }while(!line.isEmpty());
        return headers;
    }

    private void cleanup() {
        System.out.println("ConnectionHandler: ... cleaning up and exiting ... ");
        try {
            br.close();
            is.close();
            conn.close();
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler:cleanup " + ioe.getMessage());
        }
    }
}
