package app.gozenko.client;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.handler.ClientErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class DealClient {

    private final RestClient restClient;
    private final ClientErrorHandler errorHandler;
    private final String URI_STATEMENT;
    private final String URI_OFFER;

    @Autowired
    public DealClient(RestClient restClient, ClientErrorHandler errorHandler,
                      @Value("${app.gozenko.uri-statement}") String URI_STATEMENT,
                      @Value("${app.gozenko.uri-offer}") String URI_OFFER) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
        this.URI_STATEMENT = URI_STATEMENT;
        this.URI_OFFER = URI_OFFER;
    }

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState) {
        return restClient.post()
                .uri(URI_STATEMENT)
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

    public void selectOffer(LoanOfferDto loanOffer){
        restClient.post()
                .uri(URI_OFFER)
                .body(loanOffer)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .body(Void.class);
    }
}
