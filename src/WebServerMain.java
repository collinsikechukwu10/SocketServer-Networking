public class WebServerMain {

    public static void main(String[] args) {
        // TODO - implement 404 response for not found (make an error page)
        //  - Returning of binary images (GIF, JPEG and PNG)
        //  Multithreading – support multiple concurrent client connection requests up to a specified limit
        //  Logging  –  each  time  requests  are  made,  log  them  to  a  file,  indicating  date/time  request  type, response code etc.
        //  Supporting other methods in addition to GET and HEAD
        if (args.length != 0) {
            String path = args[0];
            int port = parsePort(args[1]);
            if (port != -1) {

            } else {
                System.out.println("Invalid port provided");
            }

        } else {
            System.out.println("Usage: java WebServerMain <document_root> <port>");

        }
    }

    private static int parsePort(String portString) {
        try {
            return Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}