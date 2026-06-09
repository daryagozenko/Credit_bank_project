package app.gozenko.handler;

import app.gozenko.exception.CalculatorClientException;
import app.gozenko.exception.CalculatorServerException;
import app.gozenko.exception.ClientExistsException;
import app.gozenko.exception.JsonException;
import app.gozenko.exception.NotVerifyCodeException;
import app.gozenko.exception.UnloadedDataException;
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

    @ExceptionHandler(UnloadedDataException.class)
    public ResponseEntity<Map<String, String>> handleUnloadedDataException(UnloadedDataException ex) {
        log.warn("Unloaded data from calculator");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CalculatorServerException.class)
    public ResponseEntity<Map<String, String>> handleCalculatorServerException(CalculatorServerException ex) {
        log.warn("Calculator server error");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JsonException.class)
    public ResponseEntity<Map<String, String>> handleJsonException(JsonException ex) {
        log.warn("Json error given in the calculator");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityException(EntityNotFoundException ex) {
        log.warn("Entity is not exists");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClientExistsException.class)
    public ResponseEntity<Map<String, String>> handleClientExistsException(ClientExistsException ex) {
        log.warn("Client is not exists");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception ex) {
        log.warn("Validation error");
        if (ex instanceof MethodArgumentNotValidException e) {
            Map<String, String> errors = new HashMap<>();
            e.getBindingResult().getFieldErrors()
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return createResponseEntity(errors, HttpStatus.BAD_REQUEST);
        }

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

    @ExceptionHandler(CalculatorClientException.class)
    public ResponseEntity<Map<String, String>> handleCalculatorClientValidationExceptions(
            CalculatorClientException ex) {
        log.warn("Calculator client error");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotVerifyCodeException.class)
    public ResponseEntity<Map<String, String>> handleCalculatorClientValidationExceptions(
            NotVerifyCodeException ex) {
        log.warn("Verify codes do not matches");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
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
