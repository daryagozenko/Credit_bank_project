package app.gozenko.client;

import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.handler.CommonErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class DealClient {

    private final RestClient restClient;
    private final CommonErrorHandler errorHandler;
    @Value("${app.gozenko.uri-credit}")
    private String URI_CREDIT;
    @Value("${app.gozenko.uri-offer}")
    private String URI_OFFER;
    @Value("${app.gozenko.uri-send-documents}")
    private String URI_SEND_DOCUMENTS;
    @Value("${app.gozenko.uri-sign-documents}")
    private String URI_SIGN_DOCUMENTS;
    @Value("${app.gozenko.uri-verify}")
    private String URI_VERIFY;

    public DealClient(@Qualifier("dealClient") RestClient restClient,
                      CommonErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public void calculateCredit(
            UUID statementId,
            FinishRegistrationRequestDto finishRegistration) {
        restClient.post()
                .uri(URI_CREDIT, statementId)
                .body(finishRegistration)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .body(Void.class);

    }

    public void sendDocuments(UUID statementId) {
        restClient.post()
                .uri(URI_SEND_DOCUMENTS, statementId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .body(Void.class);
    }

    public void signDocuments(UUID statementId) {
        restClient.post()
                .uri(URI_SIGN_DOCUMENTS, statementId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .body(Void.class);
    }

    public void verifyCode(UUID statementId, Integer code) {
        restClient.post()
                .uri(URI_VERIFY, statementId, code)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .body(Void.class);
    }
}
