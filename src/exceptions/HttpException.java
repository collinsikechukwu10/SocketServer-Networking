package exceptions;

import enums.RequestMethod;

/**
 * Http Exception Class.
 * This is the base exception for all implemented http exceptions.
 * Do not use this class directly, Subclass it using the type of error.
 * @author 210032207
 */
public class HttpException extends Exception {
    private final int errorCode;
    private final RequestMethod requestMethod;

    /**
     * Constructor specifying request method, error code and error message.
     *
     * @param requestMethod request method enum
     * @param errorCode     http error code
     * @param s             error message
     */
    public HttpException(RequestMethod requestMethod, int errorCode, String s) {
        super(s);
        this.errorCode = errorCode;
        this.requestMethod = requestMethod;
    }

    /**
     * Get the error code used.
     *
     * @return error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Get the request method used.
     *
     * @return request method
     */
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }
}
