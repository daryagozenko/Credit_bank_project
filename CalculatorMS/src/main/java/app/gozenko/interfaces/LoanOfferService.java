package app.gozenko.interfaces;

import app.gozenko.dto.LoanOfferDto;

import java.math.BigDecimal;
import java.util.List;

public interface LoanOfferService {
    List<?> createLoanOffers(BigDecimal amount, Integer term);
}
