package app.gozenko.interfaces;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CalculatorController {
    ResponseEntity<List<?>> calcConditionOfCredit(LoanStatementRequestDto loanState);
    CreditDto validateAndCalc(ScoringDataDto scoringData);
}
