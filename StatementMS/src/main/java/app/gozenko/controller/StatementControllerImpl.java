package app.gozenko.controller;

import app.gozenko.controller.interfaces.StatementController;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.service.DealCallingService;
import app.gozenko.service.interfaces.PreScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/statement")
public class StatementControllerImpl implements StatementController {

    private final DealCallingService dealCallingService;
    private final PreScoringService preScoringService;

    @PostMapping("/")
    @Override
    public ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(LoanStatementRequestDto loanState) {
        log.info("Input data in calcConditionOfCredit loanState-{}", loanState);
        log.debug("Sending a LoanStatementRequest to preScoringService");
        preScoringService.preScoringLoan(loanState);

        log.info("Successful create list of loanOffers");

        return ResponseEntity.status(HttpStatus.OK)
                .body(dealCallingService.getLoanOffers(loanState));
    }

    @PostMapping("/offer")
    @Override
    public ResponseEntity<Void> selectLoanOffer(LoanOfferDto loanOffer) {
        return null;
    }
}
