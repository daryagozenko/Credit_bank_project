package app.gozenko.client;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.handler.CommonErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class StatementClient {

    private final RestClient restClient;
    private final CommonErrorHandler errorHandler;
    @Value("${app.gozenko.path-statement}")
    private String path_statement;
    @Value("${app.gozenko.path-offer}")
    private String path_offer;

    public StatementClient(@Qualifier("statementClientBean") RestClient restClient,
                           CommonErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState) {
        return restClient.post()
                .uri(path_statement)
                .body(loanState)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {
                })
                .getBody();
    }

    public void selectOffer(LoanOfferDto loanOffer) {
        restClient.post()
                .uri(path_offer)
                .body(loanOffer)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .body(Void.class);
    }
}
