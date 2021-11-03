import java.io.IOException;
import java.util.logging.*;

/**
 * A sloppy singleton implementation of the logger can be made as
 * the Javadoc for Logger states that "All methods on Logger are multi-thread safe."
 */
public class LoggerService {

    private static Logger LOGGER;
    private static LoggerService INSTANCE;


    public LoggerService() {
        LOGGER = Logger.getLogger("Java Server");
        SimpleFormatter formatter = new SimpleFormatter();
        try {
            FileHandler fileHandler = new FileHandler(ServerConfig.LogFilePath, true);
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(Level.FINE);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            // if error creating file, then create a standard console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            consoleHandler.setLevel(Level.FINE);
            LOGGER.addHandler(consoleHandler);
        }
        LOGGER.setLevel(Level.FINE);

        //Part of making this class a singleton
        INSTANCE = this;

    }

    public static LoggerService getInstance() {
        if (INSTANCE == null){
            INSTANCE = new LoggerService();
        }
        return INSTANCE;
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
