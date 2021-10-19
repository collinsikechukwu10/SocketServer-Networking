package exceptions;

public class HttpException extends Exception {
    private final int errorCode;

    public HttpException(String s, int errorCode) {
        super(s);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
