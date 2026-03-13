package app.gozenko.controller;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.interfaces.CalculatorController;
import app.gozenko.service.LoanOfferServiceImpl;
import app.gozenko.service.PreScoringServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/calculator")
public class CalculatorControllerImpl implements CalculatorController {

    private final PreScoringServiceImpl preScoringService;
    private final LoanOfferServiceImpl loanOfferService;

    public CalculatorControllerImpl(PreScoringServiceImpl preScoringService, LoanOfferServiceImpl loanOfferService) {
        this.preScoringService = preScoringService;
        this.loanOfferService = loanOfferService;
    }

    @PostMapping("/offers")
    @Override
    public ResponseEntity<List<?>> calcConditionOfCredit(@RequestBody LoanStatementRequestDto loanState){
        preScoringService.preScoring(loanState);
        return ResponseEntity.ok(loanOfferService.createLoanOffers(
                loanState.getAmount(),
                loanState.getTerm()));
    }

    @PostMapping("/calc")
    @Override
    public CreditDto validateAndCalc(ScoringDataDto scoringData){

        return new CreditDto();
    }
}
