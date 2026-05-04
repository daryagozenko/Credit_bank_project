package app.gozenko.controller.interfaces;

import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface DealController {
    ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(@RequestBody LoanStatementRequestDto loanState);

    ResponseEntity<Void> selectLoanOffer(@RequestBody LoanOfferDto loanOffer);

    ResponseEntity<Void> calculateCredit(
            @PathVariable("statementId") UUID statementId,
            @RequestBody @Valid FinishRegistrationRequestDto finishRegistration);

    ResponseEntity<Void> verifyCode(@PathVariable("statementId") UUID statementId,
                                    @PathVariable("code") Integer code);

    ResponseEntity<Void> signDocuments(@PathVariable("statementId") UUID statementId);

    ResponseEntity<Void> sendDocuments(@PathVariable("statementId") UUID statementId);
}
