package exceptions;

import enums.RequestMethod;

/**
 * Internal Server Error Exception class.
 *
 * @author 210032207
 */
public class InternalServerErrorException extends HttpException {
    /**
     * Internal server error status code.
     */
    private static final int INTERNAL_SERVER_ERROR_STATUS_CODE = 500;

    /**
     * Constructor specifying request method and error message.
     *
     * @param requestMethod request method
     * @param s             error message
     */
    public InternalServerErrorException(RequestMethod requestMethod, String s) {
        super(requestMethod, INTERNAL_SERVER_ERROR_STATUS_CODE, s);
    }

    /**
     * Constructor specifying request method.
     *
     * @param requestMethod request method
     */
    public InternalServerErrorException(RequestMethod requestMethod) {
        this(requestMethod, "Internal Server Error");
    }

    /**
     * Constructor with no arguments.
     * Sets request method to undefined
     */
    public InternalServerErrorException() {
        this(RequestMethod.UNDEFINED);
    }
}
