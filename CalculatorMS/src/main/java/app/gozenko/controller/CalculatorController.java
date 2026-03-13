package app.gozenko.controller;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.service.LoanOfferService;
import app.gozenko.service.PreScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final PreScoringService preScoringService;
    private final LoanOfferService loanOfferService;

    public CalculatorController(PreScoringService preScoringService, LoanOfferService loanOfferService) {
        this.preScoringService = preScoringService;
        this.loanOfferService = loanOfferService;
    }

    @PostMapping("/offers")

    public ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(@RequestBody LoanStatementRequestDto loanState){
        preScoringService.preScoring(loanState);
        return ResponseEntity.ok(loanOfferService.createLoanOffers(
                loanState.getAmount(),
                loanState.getTerm()));
    }

    @PostMapping("/calc")
    public CreditDto validateAndCalc(ScoringDataDto scoringData){

        return new CreditDto();
    }
}
