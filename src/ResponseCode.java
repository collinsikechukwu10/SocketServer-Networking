import java.util.Arrays;

public enum ResponseCode {

    Success(200, "OK"),
    NotFound(404, "Not Found"),
    NotImplemented(501, "Not Implemented");

    private final String statusResponse;
    private final int code;
    private final String message;


    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
        this.statusResponse = "HTTP/1.1 " + this.code + " " + this.message;

    }

    public String getStatusResponse() {
        return statusResponse;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ResponseCode resolveResponseCode(int code){
        return Arrays.stream(ResponseCode.values()).filter(rc->rc.getCode()==code).findAny().orElse(null);
    }
}
