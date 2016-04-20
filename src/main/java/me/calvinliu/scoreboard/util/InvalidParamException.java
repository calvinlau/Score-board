package me.calvinliu.scoreboard.util;

/**
 * Exception that represents an invalid parameter from a request
 *
 * @author adarrivi
 */
public class InvalidParamException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidParamException(String message) {
        super(message);
    }

    public InvalidParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
