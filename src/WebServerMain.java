import java.io.File;
import java.nio.file.Paths;

public class WebServerMain {
    private final static LoggerService loggerService = LoggerService.getInstance();

    public static void main(String[] args) {
        //  Multithreading – support multiple concurrent client connection requests up to a specified limit
        if (args.length != 0) {
            String path = args[0];
            int port = resolvePort(args[1]);
            if (port > 0) {
                if (staticFilePathExists(path)) {
                    WebServer server = new WebServer(path, port);
                } else {
                    loggerService.error("File path provided does not exist");
                }
            } else {
                loggerService.error("Invalid port provided");
            }
        } else {
            loggerService.error("Usage: java WebServerMain <document_root> <port>");
        }
    }

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