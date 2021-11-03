package exceptions;

import enums.RequestMethod;

/**
 * Request Not Supported Exception.
 * Used when the request method is not supported
 *
 * @author 210032207
 */
public class RequestNotSupportedException extends HttpException {
    /**
     * Constructor specifying request method and error message.
     *
     * @param requestMethod request method
     * @param s             error message
     */
    public RequestNotSupportedException(RequestMethod requestMethod, String s) {
        super(requestMethod, 501, s);
    }

    /**
     * Constructor specifying request method.
     *
     * @param requestMethod request method
     */
    public RequestNotSupportedException(RequestMethod requestMethod) {
        this(requestMethod, "Request is not supported");
    }

    /**
     * Constructor with no arguments.
     * Sets request method to undefined
     */
    public RequestNotSupportedException() {
        this(RequestMethod.UNDEFINED);
    }

}
