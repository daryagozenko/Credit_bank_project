package app.gozenko.handler;

import app.gozenko.exception.CalculatorClientException;
import app.gozenko.exception.CalculatorServerException;
import app.gozenko.exception.JsonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ClientErrorHandler {

    private final ObjectMapper objectMapper;

    public void handleErrors(ClientHttpResponse response, HttpStatusCode status) {
        try {
            String errorBody = new String(response.getBody().readAllBytes());
            String errorMessage = parseErrorMessage(errorBody);

            if(status.is4xxClientError()){
                throw new CalculatorClientException(errorMessage);
            }

            throw new CalculatorServerException(errorMessage);
        } catch (IOException ex) {
            throw new JsonException("Ошибка в микросервисе калькулятора "+ex);
        }
    }

    private String parseErrorMessage(String errorBody) {
        try {
            JsonNode root = objectMapper.readTree(errorBody);

            for (String fieldName : new String[]{"Отказ по причине: ", "message: ", "error: ", "error", "Ошибка: "}) {
                if (root.has(fieldName)) {
                    String message = root.get(fieldName).asText();
                    if (message != null && !message.isEmpty()) {
                        return message;
                    }
                }
            }

            return errorBody;

        } catch (Exception e) {
            throw new JsonException(e.getMessage());
        }
    }
}
