package app.gozenko.exception;

public class UnloadedDataException extends RuntimeException {
    public UnloadedDataException(String message) {
        super(message);
    }
}
