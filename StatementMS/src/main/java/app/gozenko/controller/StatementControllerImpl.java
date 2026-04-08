package app.gozenko.controller;

import app.gozenko.controller.interfaces.StatementController;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vi/statement")
public class StatementControllerImpl implements StatementController {

    @PostMapping("/")
    @Override
    public ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(LoanStatementRequestDto loanState) {
        return null;
    }

    @PostMapping("/offer")
    @Override
    public ResponseEntity<Void> selectLoanOffer(LoanOfferDto loanOffer) {
        return null;
    }
}
