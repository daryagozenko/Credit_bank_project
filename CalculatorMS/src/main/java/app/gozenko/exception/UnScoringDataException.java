package app.gozenko.exception;

import lombok.Getter;

@Getter
public class UnScoringDataException extends RuntimeException {
    public UnScoringDataException(String message) {
        super(message);
    }
}
