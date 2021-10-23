package exceptions;

import enums.RequestMethod;

public class ResourceNotFoundException extends HttpException {

    public ResourceNotFoundException(RequestMethod requestMethod, String s) {
        super(requestMethod, 404, s);
    }

    public ResourceNotFoundException(RequestMethod requestMethod) {
        this(requestMethod, "Resource Not Found");
    }

    public ResourceNotFoundException() {
        this(RequestMethod.UNDEFINED);
    }
}
