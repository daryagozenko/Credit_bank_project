package app.gozenko.service.interfaces;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;

import java.util.List;
import java.util.UUID;

public interface CalculatorCallingService {
    CreditDto calcCredit(ScoringDataDto scoringData);

    List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState, UUID statementId);
}
