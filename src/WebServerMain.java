import java.io.File;
import java.nio.file.Paths;

/**
 * Web Server Main class.
 *
 * @author 210032207
 */
public class WebServerMain {

    /**
     * This is the main entrypoint of the program.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        //  Multithreading – support multiple concurrent client connection requests up to a specified limit
        if (args.length != 0) {
            String path = args[0];
            int port = resolvePort(args[1]);
            if (port > 0) {
                if (staticFilePathExists(path)) {
                    new WebServer(path, port);
                } else {
                    System.out.println("File path provided does not exist");
                }
            } else {
                System.out.println("Invalid port provided");
            }
        } else {
            System.out.println("Usage: java WebServerMain <document_root> <port>");
        }
    }

    /**
     * Resolves the http server port you want to allocate from the command line argument.
     *
     * @param portString string containing port
     * @return port number
     */
    private static int resolvePort(String portString) {
        try {
            return Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * A check to make sure that the file path provided exists and is a directory.
     *
     * @return if the conditions described ar met
     */
    private static boolean staticFilePathExists(String filePath) {
        File fileDirectory = Paths.get("").toAbsolutePath().resolve(filePath).normalize().toFile();
        return fileDirectory.exists() && fileDirectory.isDirectory();
    }

}
