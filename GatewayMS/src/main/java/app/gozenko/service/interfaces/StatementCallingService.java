package app.gozenko.service.interfaces;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;

import java.util.List;

public interface StatementCallingService {
    List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState);

    void selectLoanOffer(LoanOfferDto loanOffer);
}
