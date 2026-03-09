package app.gozenko.Controllers;

import app.gozenko.DTO.CreditDto;
import app.gozenko.DTO.LoanOfferDto;
import app.gozenko.DTO.LoanStatementRequestDto;
import app.gozenko.DTO.ScoringDataDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    @PostMapping("/offers")
    public List<LoanOfferDto> calcConditionOfCredit(LoanStatementRequestDto loanState){


        return new ArrayList<>();
    }

    @PostMapping("/calc")
    public CreditDto validateAndCalc(ScoringDataDto scoringData){

        return new CreditDto();
    }
}
