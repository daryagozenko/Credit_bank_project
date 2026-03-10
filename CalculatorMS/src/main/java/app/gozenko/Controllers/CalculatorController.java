package app.gozenko.Controllers;

import app.gozenko.DTO.CreditDto;
import app.gozenko.DTO.LoanOfferDto;
import app.gozenko.DTO.LoanStatementRequestDto;
import app.gozenko.DTO.ScoringDataDto;
import app.gozenko.Services.LoanOfferService;
import app.gozenko.Services.PreScoringService;
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
    public List<LoanOfferDto> calcConditionOfCredit(@RequestBody LoanStatementRequestDto loanState){
        preScoringService.preScoring(loanState);
        return loanOfferService.createLoanOffers(
                loanState.getAmount(),
                loanState.getTerm());
    }

    @PostMapping("/calc")
    public CreditDto validateAndCalc(ScoringDataDto scoringData){

        return new CreditDto();
    }
}
