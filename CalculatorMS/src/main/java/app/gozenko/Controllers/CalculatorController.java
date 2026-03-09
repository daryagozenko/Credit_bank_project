package app.gozenko.Controllers;

import app.gozenko.DTO.CreditDto;
import app.gozenko.DTO.LoanOfferDto;
import app.gozenko.DTO.LoanStatementRequestDto;
import app.gozenko.DTO.ScoringDataDto;
import app.gozenko.Services.PreScoringService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final PreScoringService preScoringService;

    public CalculatorController(PreScoringService preScoringService) {
        this.preScoringService = preScoringService;
    }

    @PostMapping("/offers")
    public List<LoanOfferDto> calcConditionOfCredit(LoanStatementRequestDto loanState){
        preScoringService.preScoring(loanState);

        return new ArrayList<>();
    }

    @PostMapping("/calc")
    public CreditDto validateAndCalc(ScoringDataDto scoringData){

        return new CreditDto();
    }
}
