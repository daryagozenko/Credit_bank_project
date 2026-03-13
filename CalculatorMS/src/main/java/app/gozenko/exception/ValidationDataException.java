package app.gozenko.exception;

import lombok.Getter;

@Getter
public class ValidationDataException extends RuntimeException {
    private final String field;
    private final String message;

    public ValidationDataException(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }
}
