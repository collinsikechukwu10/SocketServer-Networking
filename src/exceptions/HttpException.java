package exceptions;

import enums.RequestMethod;

public class HttpException extends Exception {
    private final int errorCode;
    private final RequestMethod requestMethod;

    public HttpException(RequestMethod requestMethod, int errorCode, String s) {
        super(s);
        this.errorCode = errorCode;
        this.requestMethod = requestMethod;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }
}
