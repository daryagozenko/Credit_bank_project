package app.gozenko.controller.interfaces;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface StatementController {
    ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(@RequestBody @Valid LoanStatementRequestDto loanState);

    ResponseEntity<Void> selectLoanOffer(@RequestBody @Valid LoanOfferDto loanOffer);
}
