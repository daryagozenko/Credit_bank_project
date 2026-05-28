package app.gozenko.handler;

import app.gozenko.exception.CommonClientException;
import app.gozenko.exception.CommonServerException;
import app.gozenko.exception.JsonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String SPLITTER = ".";

    @ExceptionHandler(JsonException.class)
    public ResponseEntity<Map<String, String>> handleJsonException(JsonException ex) {
        log.warn("Json error given in the calculator");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommonServerException.class)
    public ResponseEntity<Map<String, String>> handleCommonServerException(CommonServerException ex) {
        log.warn("Server error in gateway");
        return createResponseEntity(Map.of("error: ", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommonClientException.class)
    public ResponseEntity<Map<String, String>> handleCommonClientValidationExceptions(
            CommonClientException ex) {
        log.warn("Client error in gateway");
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
