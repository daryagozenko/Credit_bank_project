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
    @Value("${app.gozenko.path-offers}")
    private String path_offers;
    @Value("${app.gozenko.path-calc}")
    private String path_calc;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState) {
        return restClient.post()
                .uri(path_offers)
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
                .uri(path_calc)
                .body(scoringData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode()))
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode()))
                .body(CreditDto.class);
    }
}
