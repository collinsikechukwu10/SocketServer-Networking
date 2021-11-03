package enums;

import java.util.Arrays;

/**
 * Response code enum.
 *
 * @author 210032207
 */
public enum ResponseCode {
    /**
     * Success Response Code.
     */
    Success(200, "OK"),
    /**
     * Resource Not Found Response Code.
     */
    NotFound(404, "Not Found"),
    /**
     * Not Implemented Response Code.
     */
    NotImplemented(501, "Not Implemented");

    private final String statusResponse;
    private final int code;
    private final String message;

    /**
     * Constructor specifying status code and status message.
     *
     * @param code    status code
     * @param message status message
     */
    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
        this.statusResponse = "HTTP/1.1 " + this.code + " " + this.message;

    }

    /**
     * Get status response.
     *
     * @return status response
     */
    public String getStatusResponse() {
        return statusResponse;
    }

    /**
     * Get status code.
     *
     * @return status code
     */
    public int getCode() {
        return code;
    }

    /**
     * Get status message.
     *
     * @return status message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get response code enum from status code.
     *
     * @param code status code
     * @return response code enum
     */
    public static ResponseCode resolveResponseCode(int code) {
        return Arrays.stream(ResponseCode.values()).filter(rc -> rc.getCode() == code).findAny().orElse(null);
    }
}
