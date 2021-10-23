package exceptions;

import enums.RequestMethod;

public class InternalServerErrorException extends HttpException {


    public InternalServerErrorException(RequestMethod requestMethod, String s) {
        super(requestMethod, 500, s);
    }

    public InternalServerErrorException(RequestMethod requestMethod) {
        this(requestMethod, "Internal Server Error");
    }

    public InternalServerErrorException() {
        this(RequestMethod.UNDEFINED);
    }
}