package app.gozenko.controller;

import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.interfaces.CalculatorController;
import app.gozenko.interfaces.LoanOfferService;
import app.gozenko.interfaces.PreScoringService;
import app.gozenko.interfaces.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
public class CalculatorControllerImpl implements CalculatorController {

    private final PreScoringService preScoringService;
    private final LoanOfferService loanOfferService;
    private final ScoringService scoringService;


    @PostMapping("/offers")
    @Override
    public ResponseEntity<List<?>> calcConditionOfCredit(@RequestBody LoanStatementRequestDto loanState){
        preScoringService.preScoringLoan(loanState);
        return ResponseEntity.ok(loanOfferService.createLoanOffers(
                loanState.getAmount(),
                loanState.getTerm()));
    }

    @PostMapping("/calc")
    @Override
    public ResponseEntity<?> validateAndCalc(@RequestBody ScoringDataDto scoringData){
        preScoringService.preScoringScoreData(scoringData);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(scoringService.createScoringData(scoringData));
    }
}
