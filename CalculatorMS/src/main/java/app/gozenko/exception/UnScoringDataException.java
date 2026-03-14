package app.gozenko.exception;

import lombok.Getter;

@Getter
public class UnScoringDataException extends RuntimeException {
    private final String message;

    public UnScoringDataException(String message) {
        super(message);
        this.message = message;
    }
}
