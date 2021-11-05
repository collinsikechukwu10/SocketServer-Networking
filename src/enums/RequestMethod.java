package enums;

import java.util.Arrays;

/**
 * Request Method Enum Class.
 * Only the implemented request methods are included here.
 *
 * @author 210032207
 */
public enum RequestMethod {
    /**
     * Get Request Method.
     */
    GET,
    /**
     * Post Request Method.
     */
    POST,
    /**
     * Head Request Method.
     */
    HEAD,
    /**
     * Delete Request Method.
     */
    DELETE,
    /**
     * Undefined Request Method.
     * Used for all request methods that are not handled by our http server.
     */
    UNDEFINED;

    /**
     * Gets an appropriate request method enum using the request method string.
     *
     * @param requestMethod request method
     * @return request method enum
     */
    public static RequestMethod resolveRequestMethod(String requestMethod) {
        return Arrays.stream(RequestMethod.values()).filter(rc -> rc.name().equalsIgnoreCase(requestMethod.strip())).findAny().orElse(UNDEFINED);
    }
}
