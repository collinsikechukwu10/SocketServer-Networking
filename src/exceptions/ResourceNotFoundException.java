package exceptions;

import enums.RequestMethod;

/**
 * Resource Not Found Exception class.
 *
 * @author 210032207
 */
public class ResourceNotFoundException extends HttpException {
    /**
     * Resource not found status code.
     */
    private static final int RESOURCE_NOT_FOUND_STATUS_CODE = 404;

    /**
     * Constructor specifying request method and error message.
     *
     * @param requestMethod request method
     * @param s             error message
     */
    public ResourceNotFoundException(RequestMethod requestMethod, String s) {
        super(requestMethod, RESOURCE_NOT_FOUND_STATUS_CODE, s);
    }

    /**
     * Constructor specifying request method.
     *
     * @param requestMethod request method
     */
    public ResourceNotFoundException(RequestMethod requestMethod) {
        this(requestMethod, "Resource Not Found");
    }

    /**
     * Constructor with no arguments.
     * Sets request method to undefined
     */
    public ResourceNotFoundException() {
        this(RequestMethod.UNDEFINED);
    }
}
