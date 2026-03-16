package app.gozenko.handler;

import app.gozenko.exception.UnScoringDataException;
import app.gozenko.exception.ValidationDataException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionValidationHandler {


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> mismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> constraintException(ConstraintViolationException ex) {
        Map<String, String> exceptions = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String field = extractFieldName(violation.getPropertyPath().toString());
            String message = violation.getMessage();
            exceptions.put(field, message);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exceptions);
    }

    @ExceptionHandler(ValidationDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> legalAgeException(ValidationDataException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getField()+": "+ex.getMessage());
    }

    @ExceptionHandler(UnScoringDataException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> scoringException(UnScoringDataException ex){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Отказ: по причине "+ex.getMessage());
    }

    private String extractFieldName(String propertyPath) {
        String[] parts = propertyPath.split("\\.");

        return parts[parts.length - 1];
    }
}
