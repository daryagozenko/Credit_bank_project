package app.gozenko.exception;

public class CommonServerException extends RuntimeException {
    public CommonServerException(String message) {
        super(message);
    }
}
