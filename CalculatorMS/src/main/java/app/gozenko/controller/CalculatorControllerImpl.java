package app.gozenko.controller;

import app.gozenko.dto.CreditDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
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
        log.info("Sending a LoanStatementRequest to preScoringService");
        preScoringService.preScoringLoan(loanState);
        log.info("Successful create list of loanOffers");
        return ResponseEntity.status(HttpStatus.OK)
                .body(loanOfferService.createLoanOffers(
                        loanState.getAmount(),
                        loanState.getTerm()));
    }

    @PostMapping("/calc")
    @Operation(summary = "расчет полной стоимости кредита и графика платежей")
    @Override
    public ResponseEntity<CreditDto> validateAndCalc(@RequestBody ScoringDataDto scoringData){
        log.info("Sending a ScoringData to preScoringService");
        preScoringService.preScoringScoreData(scoringData);
        log.info("Successful create credit offer");
        return ResponseEntity.status(HttpStatus.OK)
                .body(scoringService.createScoringData(scoringData));
    }
}
