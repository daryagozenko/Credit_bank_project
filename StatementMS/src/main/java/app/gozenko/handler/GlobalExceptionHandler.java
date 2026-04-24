package app.gozenko.handler;

import app.gozenko.exception.DealClientException;
import app.gozenko.exception.DealServerException;
import app.gozenko.exception.ValidationDataException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String SPLITTER = ".";

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception ex) {
        log.warn("Validation error");
        if (ex instanceof MethodArgumentNotValidException) {
            Map<String, String> errors = new HashMap<>();
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            e.getBindingResult().getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return createResponseEntity(errors, HttpStatus.BAD_REQUEST);
        }

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

        return createResponseEntity(Map.of("message: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, String>> handleDataParseException(DateTimeParseException ex) {
        log.warn("DateTime parse error");
        String message = "Дата должна соответствовать формату yyyy-mm-dd";
        return createResponseEntity(Map.of("message: ", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationDataException.class)
    public ResponseEntity<Map<String, String>> handleValidationDataException(ValidationDataException ex) {
        log.warn("Validation on scoring data error");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DealServerException.class)
    public ResponseEntity<Map<String, String>> handleDealServerException(DealServerException ex) {
        log.warn("Deal server error");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DealClientException.class)
    public ResponseEntity<Map<String, String>> handleDealClientException(DealClientException ex) {
        log.warn("Deal client error");
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
