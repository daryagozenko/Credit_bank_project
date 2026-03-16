package app.gozenko.exception;

import lombok.Getter;

@Getter
public class ValidationDataException extends RuntimeException {
    public ValidationDataException(String message) {
        super(message);
    }
}
