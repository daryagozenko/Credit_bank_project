package app.gozenko.controller.interfaces;

import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface DealController {
    ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(@Valid LoanStatementRequestDto loanState);

    ResponseEntity<Void> selectLoanOffer(@Valid LoanOfferDto loanOffer);

    ResponseEntity<Void> calculateCredit(
            UUID statementId,
            @Valid FinishRegistrationRequestDto finishRegistration);
}
