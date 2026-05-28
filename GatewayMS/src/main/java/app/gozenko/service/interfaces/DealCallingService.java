package app.gozenko.service.interfaces;

import app.gozenko.dto.FinishRegistrationRequestDto;

import java.util.UUID;

public interface DealCallingService {
    void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistration);

    void sendDocuments(UUID statementId);

    void signDocuments(UUID statementId);

    void verifyCode(UUID statementId, Integer code);
}
