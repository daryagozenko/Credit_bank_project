package app.gozenko.controller.interfaces;

import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface GatewayController {
    ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(LoanStatementRequestDto loanState);

    ResponseEntity<Void> selectLoanOffer(LoanOfferDto loanOffer);

    ResponseEntity<Void> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistration);

    ResponseEntity<Void> sendDocuments(UUID statementId);

    ResponseEntity<Void> verifyCode(UUID statementId, Integer code);
}
