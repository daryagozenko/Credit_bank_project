package app.gozenko.controller;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.controller.interfaces.CalculatorController;
import app.gozenko.service.interfaces.LoanOfferService;
import app.gozenko.service.interfaces.PreScoringService;
import app.gozenko.service.interfaces.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
@Tag(name = "CalculatorController")
public class CalculatorControllerImpl implements CalculatorController {

    private final PreScoringService preScoringService;
    private final LoanOfferService loanOfferService;
    private final ScoringService scoringService;


    @PostMapping("/offers")
    @Operation(summary = "создание кредитной заявки")
    @Override
    public ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(@RequestBody LoanStatementRequestDto loanState){
        preScoringService.preScoringLoan(loanState);
        return ResponseEntity.ok(loanOfferService.createLoanOffers(
                loanState.getAmount(),
                loanState.getTerm()));
    }

    @PostMapping("/calc")
    @Operation(summary = "расчет полной стоимости кредита и графика платежей")
    @Override
    public ResponseEntity<?> validateAndCalc(@RequestBody ScoringDataDto scoringData){
        preScoringService.preScoringScoreData(scoringData);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(scoringService.createScoringData(scoringData));
    }
}
