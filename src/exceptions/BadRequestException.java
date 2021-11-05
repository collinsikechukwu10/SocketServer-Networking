package exceptions;

import enums.RequestMethod;

/**
 * Bad Request Exception class.
 *
 * @author 210032207
 */
public class BadRequestException extends HttpException {
    /**
     * Internal server error status code.
     */
    private static final int BAD_REQUEST_STATUS_CODE = 400;

    /**
     * Constructor specifying request method and error message.
     *
     * @param requestMethod request method
     * @param s             error message
     */
    public BadRequestException(RequestMethod requestMethod, String s) {
        super(requestMethod, BAD_REQUEST_STATUS_CODE, s);
    }

    /**
     * Constructor specifying request method.
     *
     * @param requestMethod request method
     */
    public BadRequestException(RequestMethod requestMethod) {
        this(requestMethod, "Bad Request Error");
    }

    /**
     * Constructor with no arguments.
     * Sets request method to undefined
     */
    public BadRequestException() {
        this(RequestMethod.UNDEFINED);
    }
}
