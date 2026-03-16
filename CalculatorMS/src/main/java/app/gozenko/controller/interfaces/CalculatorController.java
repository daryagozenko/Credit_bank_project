package app.gozenko.controller.interfaces;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CalculatorController{
    ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(LoanStatementRequestDto loanState);
    ResponseEntity<?> validateAndCalc(ScoringDataDto scoringData);
}
