package exceptions;

public class RequestNotSupportedException extends HttpException{

    public RequestNotSupportedException(String s) {
        super(s, 501);
    }

}
