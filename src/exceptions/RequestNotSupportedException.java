package exceptions;

import enums.RequestMethod;

public class RequestNotSupportedException extends HttpException {

    public RequestNotSupportedException(RequestMethod requestMethod, String s) {
        super(requestMethod, 501, s);
    }

    public RequestNotSupportedException(RequestMethod requestMethod) {
        this(requestMethod, "Request is not supported");
    }

    public RequestNotSupportedException() {
        this(RequestMethod.UNDEFINED);
    }

}
