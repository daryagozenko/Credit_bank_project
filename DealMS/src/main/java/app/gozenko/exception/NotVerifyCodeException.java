package app.gozenko.exception;

public class NotVerifyCodeException extends RuntimeException {
    public NotVerifyCodeException(String message) {
        super(message);
    }
}
