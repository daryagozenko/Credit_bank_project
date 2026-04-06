package app.gozenko.client;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.exception.CalculatorClientException;
import app.gozenko.exception.CalculatorServerException;
import app.gozenko.exception.JsonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CalculatorClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    @Value("${app.gozenko.uri-offers}")
    private String URI_OFFERS;
    @Value("${app.gozenko.uri-calc}")
    private String URI_CALC;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState) {
        return restClient.post()
                .uri(URI_OFFERS)
                .body(loanState)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    handle4xxError(response);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    handle5xxError(response);
                })
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {
                })
                .getBody();
    }

    public CreditDto getCredit(ScoringDataDto scoringData) {
        return restClient.post()
                .uri(URI_CALC)
                .body(scoringData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> handle4xxError(response))
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> handle5xxError(response))
                .body(CreditDto.class);
    }

    private void handle5xxError(ClientHttpResponse response) {
        try {
            String errorBody = new String(response.getBody().readAllBytes());
            String errorMessage = parseErrorMessage(errorBody);
            throw new CalculatorServerException(errorMessage);
        } catch (IOException ex) {
            throw new JsonException("Ошибка в микросервисе калькулятора "+ex);
        }
    }

    private void handle4xxError(ClientHttpResponse response) {
        try {
            String errorBody = new String(response.getBody().readAllBytes());
            String errorMessage = parseErrorMessage(errorBody);
            throw new CalculatorClientException(errorMessage);
        } catch (IOException ex) {
            throw new JsonException("Ошибка чтения ответа из калькулятора "+ex);
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
