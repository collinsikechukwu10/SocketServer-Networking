import java.io.IOException;
import java.util.logging.*;

public class LoggerService {

    private final Logger LOGGER;

    public LoggerService(String name) {
        // probably throws ioexception if log file aint found
        LOGGER = Logger.getLogger(name);
        Formatter formatter = new SimpleFormatter();
//        try {
//            FileHandler fileHandler = new FileHandler(ServerConfig.LogFilePath,true);
//            fileHandler.setFormatter(formatter);
//            fileHandler.setLevel(Level.FINE);
//            LOGGER.addHandler(fileHandler);
//        } catch (IOException e) {
//            // if error creating file, then create a standard console handler
//            ConsoleHandler consoleHandler = new ConsoleHandler();
//            consoleHandler.setFormatter(formatter);
//            consoleHandler.setLevel(Level.FINE);
//            LOGGER.addHandler(consoleHandler);
//        }
        LOGGER.setLevel(Level.FINE);
    }

    public void info(String msg) {
//        this.LOGGER.log(Level.INFO, msg);
        System.out.println(msg);
    }

    public void error(String msg, Exception e) {
        System.out.println(msg + " " + e.getMessage());
        e.printStackTrace();
//        this.LOGGER.log(Level.SEVERE, msg, e);
    }

    public void error(String msg) {
        System.out.println(msg);

//        LOGGER.log(Level.SEVERE, msg);
    }
}
