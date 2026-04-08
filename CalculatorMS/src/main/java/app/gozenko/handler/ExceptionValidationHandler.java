package app.gozenko.handler;

import app.gozenko.exception.DateParseException;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            ValidationDataException.class})
    public ResponseEntity<Map<String, String>> handleValidationException(Exception ex) {
        if (ex instanceof ConstraintViolationException) {
            Map<String, String> exceptions = new HashMap<>();
            ConstraintViolationException exception = (ConstraintViolationException) ex;

            exception.getConstraintViolations().forEach(violation -> {
                String field = extractFieldName(violation.getPropertyPath().toString());
                String message = violation.getMessage();
                exceptions.put(field, message);
            });
            return createResponseEntity(Map.of("message: ", exceptions.toString()), HttpStatus.BAD_REQUEST);
        }

        if (ex instanceof ValidationDataException) {
            return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return createResponseEntity(Map.of("message: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnScoringDataException.class)
    public ResponseEntity<Map<String, String>> handleUnScoringDataException(UnScoringDataException ex) {
        return createResponseEntity(Map.of("Отказ по причине: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateParseException.class)
    public ResponseEntity<Map<String, String>> handleDataParseException(DateParseException ex) {
        return createResponseEntity(Map.of("message: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, String>> createResponseEntity(Map<String, String> message,
                                                                     HttpStatus status) {
        return ResponseEntity.status(status).body(message);
    }


    private String extractFieldName(String propertyPath) {
        int lastDotIndex = propertyPath.lastIndexOf(SPLITTER);
        return lastDotIndex == -1 ? propertyPath : propertyPath.substring(lastDotIndex + 1);
    }
}
