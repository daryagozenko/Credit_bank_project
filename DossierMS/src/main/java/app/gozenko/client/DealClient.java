package app.gozenko.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DealClient {

    private final RestClient restClient;
    @Value("${app.gozenko.uri-put-document-status}")
    private String URI_PUT_DOCUMENT_STATUS;

    public void putDocumentStatus(UUID statementId) {
        log.debug("Input statementId-{}", statementId);
        restClient.put()
                .uri(URI_PUT_DOCUMENT_STATUS, statementId)
                .retrieve()
                .body(Void.class);
    }
}
