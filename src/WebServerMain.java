public class WebServerMain {

    public static void main(String[] args) {
        //  Multithreading – support multiple concurrent client connection requests up to a specified limit
        if (args.length != 0) {
            String path = args[0];
            int port = parsePort(args[1]);
            if (port < 0) {
                WebServer server = new WebServer(path, port);
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