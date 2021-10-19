package exceptions;

public class InternalServerErrorException extends HttpException {

    public InternalServerErrorException(String s) {
        super(s, 500);
    }
}