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
     * Request not supported status code.
     */
    private static final int REQUEST_NOT_SUPPORTED_STATUS_CODE = 501;

    /**
     * Constructor specifying request method and error message.
     *
     * @param requestMethod request method
     * @param s             error message
     */
    public RequestNotSupportedException(RequestMethod requestMethod, String s) {
        super(requestMethod, REQUEST_NOT_SUPPORTED_STATUS_CODE, s);
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
