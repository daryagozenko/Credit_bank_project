package app.gozenko.handler;

import app.gozenko.exception.CalculatorClientException;
import app.gozenko.exception.CalculatorServerException;
import app.gozenko.exception.ClientExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String SPLITTER = ".";

    @ExceptionHandler({Exception.class,
            CalculatorServerException.class})
    public ResponseEntity<Map<String, String>> scoringException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error: ", ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> entityException(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error: ", ex.getMessage()));
    }

    @ExceptionHandler(ClientExistsException.class)
    public ResponseEntity<Map<String, String>> clientExistsException(ClientExistsException ex) {
        return createResponseEntity(ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            CalculatorClientException.class})
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
        if (ex instanceof CalculatorClientException) {
            return createResponseEntity(ex.getMessage());
        }

        return createResponseEntity(ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> createResponseEntity(String message) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message: ", message));
    }

    private String extractFieldName(String propertyPath) {
        int lastDotIndex = propertyPath.lastIndexOf(SPLITTER);
        return lastDotIndex == -1 ? propertyPath : propertyPath.substring(lastDotIndex + 1);
    }
}
