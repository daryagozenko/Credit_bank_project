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
    private final String path_statement;
    private final String path_offer;

    @Autowired
    public DealClient(RestClient restClient, ClientErrorHandler errorHandler,
                      @Value("${app.gozenko.path-statement}") String path_statement,
                      @Value("${app.gozenko.path-offer}") String path_offer) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
        this.path_statement = path_statement;
        this.path_offer = path_offer;
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

    public void selectOffer(LoanOfferDto loanOffer){
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
