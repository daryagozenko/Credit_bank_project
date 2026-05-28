package app.gozenko.service;

import app.gozenko.client.DealClient;
import app.gozenko.dto.FinishRegistrationRequestDto;
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
    public void calculateCredit(
            UUID statementId,
            FinishRegistrationRequestDto finishRegistration) {
        log.info("Input statementId-{}, finishRegistration-{}", statementId, finishRegistration);
        dealClient.calculateCredit(statementId, finishRegistration);
        log.info("Finish registration");
    }

    @Override
    public void sendDocuments(UUID statementId) {
       log.info("Input statementId in sendDocuments-{}", statementId);
       dealClient.sendDocuments(statementId);
       log.info("Sending documents");
    }

    @Override
    public void signDocuments(UUID statementId) {
        log.info("Input statementId in signDocuments-{}", statementId);
        dealClient.signDocuments(statementId);
        log.info("Signing documents");
    }

    @Override
    public void verifyCode(UUID statementId, Integer code) {
        log.info("Input statementId-{}, code-{}", statementId, code);
        dealClient.verifyCode(statementId, code);
        log.info("Verifying code");
    }
}
