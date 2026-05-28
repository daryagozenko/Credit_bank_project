package app.gozenko.service;

import app.gozenko.client.DealClient;
import app.gozenko.service.interfaces.DealCallingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealCallingServiceImpl implements DealCallingService {

    private final DealClient dealClient;

    @Override
    public void putDocumentStatus(UUID statementId) {
        log.info("Input statementId-{}", statementId);
        dealClient.putDocumentStatus(statementId);
        log.info("Put status in document successfully");
    }
}
