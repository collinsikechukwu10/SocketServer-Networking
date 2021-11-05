import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

/**
 * Logger Service class.
 * A sloppy singleton implementation of a logging system as
 * the Javadoc for Logger states that "All methods on Logger are multi-thread safe.".
 *
 * @author 210032207
 */
public class LoggerService {

    private static Logger logger;
    private static LoggerService instance;

    /**
     * Logger constructor.
     * This is used to create the logger and handlers.
     */
    public LoggerService() {
        logger = Logger.getLogger("Java Server");
        SimpleFormatter formatter = new SimpleFormatter();
        try {
            FileHandler fileHandler = new FileHandler(ServerConfig.LOG_FILE_PATH, true);
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(Level.FINE);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            // if error creating file, then create a standard console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            consoleHandler.setLevel(Level.FINE);
            logger.addHandler(consoleHandler);
        }
        logger.setLevel(Level.FINE);

        //Part of making this class a singleton
        instance = this;

    }

    /**
     * This is the main entrypoint for other classes to use the logger as this is a singleton class.
     *
     * @return loggerService class
     */
    public static LoggerService getInstance() {
        if (instance == null) {
            instance = new LoggerService();
        }
        return instance;
    }

    /**
     * Log message at INFO level.
     *
     * @param msg message
     */
    public void info(String msg) {
        logger.log(Level.INFO, msg);
        System.out.println(msg);
    }

    /**
     * Log message at ERROR level.
     *
     * @param msg message
     * @param e   Exception class, incase you want to log the stacktrace of the exception
     */
    public void error(String msg, Exception e) {

        logger.log(Level.SEVERE, msg, e);
    }

    /**
     * Log message at ERROR level.
     *
     * @param msg message
     */
    public void error(String msg) {
        logger.log(Level.SEVERE, msg);
    }
}
