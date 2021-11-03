package exceptions;

import enums.RequestMethod;

/**
 * Internal Server Error Exception class
 *
 * @author 210032207
 */
public class InternalServerErrorException extends HttpException {

    /**
     * Constructor specifying request method and error message.
     *
     * @param requestMethod request method
     * @param s             error message
     */
    public InternalServerErrorException(RequestMethod requestMethod, String s) {
        super(requestMethod, 500, s);
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