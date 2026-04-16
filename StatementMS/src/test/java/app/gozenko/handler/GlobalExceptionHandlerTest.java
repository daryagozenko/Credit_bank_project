package app.gozenko.handler;

import app.gozenko.exception.DealClientException;
import app.gozenko.exception.DealServerException;
import app.gozenko.exception.ValidationDataException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Проверка тела ответа с выбросом ошибки MethodArgumentTypeMismatchException")
    void handle_MethodArgumentTypeMismatchException() {
        MethodParameter param = mock(MethodParameter.class);
        String parameterName = "id";
        when(param.getParameterName()).thenReturn(parameterName);

        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "value",
                String.class,
                parameterName,
                param,
                null
        );

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(response.getBody().get("message: "), ex.getMessage());
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Проверка тела ответа с выбросом ошибки ConstraintViolationException")
    void handle_ConstraintViolationException() {
        ConstraintViolationException ex = new ConstraintViolationException(new HashSet<>());

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(response.getBody().get("message: "), "{}");
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Проверка тела ответа с выбросом ошибки DateTimeParseException")
    void handle_DateTimeParseException() {
        String message = "Дата должна соответствовать формату yyyy-mm-dd";
        DateTimeParseException ex = new DateTimeParseException(message, "13-11-2025", 1);
        ResponseEntity<Map<String, String>> response = handler.handleDataParseException(ex);

        assertEquals(response.getBody().get("message: "), message);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Проверка тела ответа с выбросом ошибки ValidationDataException")
    void handle_ValidationDataException() {
        String message = "Ошибка валидации";
        ValidationDataException ex = new ValidationDataException(message);
        ResponseEntity<Map<String, String>> response = handler.handleValidationDataException(ex);

        assertEquals(response.getBody().get("error: "), message);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Проверка тела ответа с выбросом ошибки DealServerException")
    void handle_DealServerException() {
        String message = "Ошибка на сервере";
        DealServerException ex = new DealServerException(message);
        ResponseEntity<Map<String, String>> response = handler.handleDealServerException(ex);

        assertEquals(response.getBody().get("error: "), message);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Проверка тела ответа с выбросом ошибки DealClientException")
    void handle_DealClientException() {
        String message = "Ошибка на клиенте";
        DealClientException ex = new DealClientException(message);
        ResponseEntity<Map<String, String>> response = handler.handleDealClientException(ex);

        assertEquals(response.getBody().get("error: "), message);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

}
