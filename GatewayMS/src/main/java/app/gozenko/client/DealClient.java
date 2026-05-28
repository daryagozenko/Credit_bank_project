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
    @Value("${app.gozenko.path-credit}")
    private String path_credit;
    @Value("${app.gozenko.path-send-documents}")
    private String path_send_documents;
    @Value("${app.gozenko.path-sign-documents}")
    private String path_sign_documents;
    @Value("${app.gozenko.path-verify}")
    private String path_verify;

    public DealClient(@Qualifier("dealClientBean") RestClient restClient,
                      CommonErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public void calculateCredit(
            UUID statementId,
            FinishRegistrationRequestDto finishRegistration) {
        restClient.post()
                .uri(path_credit, statementId)
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
                .uri(path_send_documents, statementId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .body(Void.class);
    }

    public void signDocuments(UUID statementId) {
        restClient.post()
                .uri(path_sign_documents, statementId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .body(Void.class);
    }

    public void verifyCode(UUID statementId, Integer code) {
        restClient.post()
                .uri(path_verify, statementId, code)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) ->
                        errorHandler.handleErrors(response, response.getStatusCode())))
                .body(Void.class);
    }
}
