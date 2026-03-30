package app.gozenko.client;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.exception.CalculatorClientException;
import app.gozenko.exception.CalculatorServerException;
import lombok.RequiredArgsConstructor;
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

    private RestClient restClient;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState) {
        return restClient.post()
                .uri("/offers")
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
                .uri("/calc")
                .body(scoringData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    handle4xxError(response);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    handle5xxError(response);
                })
                .body(CreditDto.class);
    }

    private void handle5xxError(ClientHttpResponse response) {
        try {
            String errorMessage = new String(response.getBody().readAllBytes());
            throw new CalculatorServerException(errorMessage);
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка в микросервисе калькулятора", ex);
        }
    }

    private void handle4xxError(ClientHttpResponse response) {
        try {
            String errorMessage = new String(response.getBody().readAllBytes());
            throw new CalculatorClientException(errorMessage);
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка чтения ответа из калькулятора", ex);
        }
    }
}
