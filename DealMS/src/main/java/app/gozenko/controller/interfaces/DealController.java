package app.gozenko.controller.interfaces;

import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface DealController {
    ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(@RequestBody @Valid LoanStatementRequestDto loanState);
    ResponseEntity<String> selectLoanOffer(@RequestBody @Valid LoanOfferDto loanOffer);
    ResponseEntity<String> calculateCredit(
            @PathVariable("statementId") UUID statementId,
            @RequestBody @Valid FinishRegistrationRequestDto finishRegistration);
}
