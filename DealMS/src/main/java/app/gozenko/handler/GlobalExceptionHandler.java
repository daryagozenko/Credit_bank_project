package app.gozenko.handler;

import app.gozenko.exception.CalculatorClientException;
import app.gozenko.exception.CalculatorServerException;
import app.gozenko.exception.ClientExistsException;
import app.gozenko.exception.JsonException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String SPLITTER = ".";

    @ExceptionHandler({CalculatorServerException.class,
            JsonException.class})
    public ResponseEntity<Map<String, String>> scoringException(CalculatorServerException ex) {
        log.warn("Scoring error given in the calculator");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error: ", ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> entityException(EntityNotFoundException ex) {
        log.warn("Entity is not exist");
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

        return createResponseEntity(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> getValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    private ResponseEntity<Map<String, String>> createResponseEntity(String message) {
        log.warn("The entered data on the client side is not valid");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message: ", message));
    }

    private String extractFieldName(String propertyPath) {
        int lastDotIndex = propertyPath.lastIndexOf(SPLITTER);
        return lastDotIndex == -1 ? propertyPath : propertyPath.substring(lastDotIndex + 1);
    }
}
