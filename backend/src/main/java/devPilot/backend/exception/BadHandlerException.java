package devPilot.backend.exception;

public class BadHandlerException extends RuntimeException {
    public BadHandlerException(String message) {
        super(message);
    }

    public BadHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}