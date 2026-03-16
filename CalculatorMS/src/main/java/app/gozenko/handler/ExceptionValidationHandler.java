package app.gozenko.handler;

import app.gozenko.exception.UnScoringDataException;
import app.gozenko.exception.ValidationDataException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionValidationHandler {

    private static final String SPLITTER = ".";

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            ValidationDataException.class})
    public ResponseEntity<Map<String, String>> getException(Exception ex) {
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return createResponseEntity(ex.getMessage());
        }
        if (ex instanceof ConstraintViolationException) {
            Map<String, String> exceptions = new HashMap<>();
            ConstraintViolationException exception = (ConstraintViolationException) ex;

            exception.getConstraintViolations().forEach(violation -> {
                String field = extractFieldName(violation.getPropertyPath().toString());
                String message = violation.getMessage();
                exceptions.put(field, message);
            });
            return createResponseEntity(exceptions.toString());
        }
        if (ex instanceof ValidationDataException) {
            return createResponseEntity("Ошибка: " + ex.getMessage());
        }
        return createResponseEntity(ex.getMessage());
    }

    @ExceptionHandler(UnScoringDataException.class)
    public ResponseEntity<Map<String, String>> scoringException(UnScoringDataException ex) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("Отказ по причине: ", ex.getMessage()));
    }

    private ResponseEntity<Map<String, String>> createResponseEntity(String message) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message: ",message));
    }


    private String extractFieldName(String propertyPath) {
        int lastDotIndex = propertyPath.lastIndexOf(SPLITTER);
        return lastDotIndex == -1 ? propertyPath : propertyPath.substring(lastDotIndex + 1);
    }
}
