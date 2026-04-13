package app.gozenko.client;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.exception.DealClientException;
import app.gozenko.exception.DealServerException;
import app.gozenko.exception.JsonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Component
public class DealClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String URI_STATEMENT;
    private final String URI_OFFER;

    @Autowired
    public DealClient(RestClient restClient, ObjectMapper objectMapper,
                      @Value("${app.gozenko.uri-statement}") String URI_STATEMENT,
                      @Value("${app.gozenko.uri-offer}") String URI_OFFER) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.URI_STATEMENT = URI_STATEMENT;
        this.URI_OFFER = URI_OFFER;
    }

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState) {
        return restClient.post()
                .uri(URI_STATEMENT)
                .body(loanState)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> handle4xxError(response)))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> handle5xxError(response)))
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {
                })
                .getBody();
    }

    public void selectOffer(LoanOfferDto loanOffer){
        restClient.post()
                .uri(URI_OFFER)
                .body(loanOffer)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> handle4xxError(response)))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> handle5xxError(response)))
                .body(Void.class);
    }

    private void handle5xxError(ClientHttpResponse response) {
        try {
            String errorBody = new String(response.getBody().readAllBytes());
            String errorMessage = parseErrorMessage(errorBody);
            throw new DealServerException(errorMessage);
        } catch (IOException ex) {
            throw new JsonException("Ошибка в микросервисе сделки " + ex);
        }
    }

    private void handle4xxError(ClientHttpResponse response) {
        try {
            String errorBody = new String(response.getBody().readAllBytes());
            String errorMessage = parseErrorMessage(errorBody);
            throw new DealClientException(errorMessage);
        } catch (IOException ex) {
            throw new JsonException("Ошибка чтения ответа из сделки " + ex);
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
