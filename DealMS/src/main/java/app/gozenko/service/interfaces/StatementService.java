package app.gozenko.service.interfaces;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Credit;
import app.gozenko.entity.Statement;
import app.gozenko.enums.StatementStatus;

import java.util.UUID;

public interface StatementService {
    Statement createStatement(Client client);
    Statement findById(UUID statementId);
    void updateStatement(LoanOfferDto loanOffer);
    void updateStatementStatusHistory(Statement statement, StatementStatus status);
    void addCredit(Statement statement, Credit credit);
    void updateStatementSesCode(Statement statement);
    Statement findByIdWithLock(UUID statementId);
}
