package app.gozenko.client;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.handler.ClientErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CalculatorClient {

    private final RestClient restClient;
    private final ClientErrorHandler errorHandler;
    @Value("${app.gozenko.uri-offers}")
    private String URI_OFFERS;
    @Value("${app.gozenko.uri-calc}")
    private String URI_CALC;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState) {
        return restClient.post()
                .uri(URI_OFFERS)
                .body(loanState)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode()))
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode()))
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {
                })
                .getBody();
    }

    public CreditDto getCredit(ScoringDataDto scoringData) {
        return restClient.post()
                .uri(URI_CALC)
                .body(scoringData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode()))
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode()))
                .body(CreditDto.class);
    }
}
