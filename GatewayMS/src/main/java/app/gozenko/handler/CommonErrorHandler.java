package app.gozenko.handler;

import app.gozenko.exception.CommonClientException;
import app.gozenko.exception.CommonServerException;
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
public class CommonErrorHandler {

    private final ObjectMapper objectMapper;

    public void handleErrors(ClientHttpResponse response, HttpStatusCode status) {
        try {
            String errorBody = new String(response.getBody().readAllBytes());
            String errorMessage = parseErrorMessage(errorBody);

            if(status.is4xxClientError()){
                throw new CommonClientException(errorMessage);
            }

            throw new CommonServerException(errorMessage);
        } catch (IOException ex) {
            throw new JsonException("Ошибка парсинга json "+ex);
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
