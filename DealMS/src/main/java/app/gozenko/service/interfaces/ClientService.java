package app.gozenko.service.interfaces;

import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Statement;

import java.util.UUID;

public interface ClientService {
    Client createClient(LoanStatementRequestDto request);
    Client getClientById(UUID clientId);
    void updateClient(Statement statement, FinishRegistrationRequestDto finishRegistration);
}
