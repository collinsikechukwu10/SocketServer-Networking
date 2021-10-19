package exceptions;

public class ResourceNotFoundException extends HttpException {

    public ResourceNotFoundException(String s) {
        super(s,404);
    }
}
