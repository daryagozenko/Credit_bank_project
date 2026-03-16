package app.gozenko.service.interfaces;

import app.gozenko.dto.LoanOfferDto;

import java.math.BigDecimal;
import java.util.List;

public interface LoanOfferService {
    List<LoanOfferDto> createLoanOffers(BigDecimal amount, Integer term);
}
