package app.gozenko.handler;

import app.gozenko.exception.UnScoringDataException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionValidationHandler {

    private static final String SPLITTER = ".";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        log.warn("Global exception dropped");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class})
    public ResponseEntity<Map<String, String>> handleValidationException(Exception ex) {
        log.warn("Validation error");
        if (ex instanceof ConstraintViolationException exception) {
            Map<String, String> exceptions = new HashMap<>();

            exception.getConstraintViolations().forEach(violation -> {
                String field = extractFieldName(violation.getPropertyPath().toString());
                String message = violation.getMessage();
                exceptions.put(field, message);
            });
            return createResponseEntity(Map.of("message: ", exceptions.toString()), HttpStatus.BAD_REQUEST);
        }

        return createResponseEntity(Map.of("message: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnScoringDataException.class)
    public ResponseEntity<Map<String, String>> handleUnScoringDataException(UnScoringDataException ex) {
        log.warn("Scoring data error");
        return createResponseEntity(Map.of("Отказ по причине: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
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
