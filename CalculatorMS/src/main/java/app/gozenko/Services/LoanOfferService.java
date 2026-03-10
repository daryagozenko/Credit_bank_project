package app.gozenko.Services;

import app.gozenko.DTO.LoanOfferDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanOfferService {
    @Value("${app.gozenko.base-rate}")
    private String propertyRate;

    public List<LoanOfferDto> createLoanOffers(BigDecimal amount, Integer term){
        List<LoanOfferDto> result = new ArrayList<>();
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal monthlyPayment = new BigDecimal(0);

        BigDecimal rate = new BigDecimal(propertyRate);
        result.add(LoanOfferDto.createWithNewId(
                amount,
                totalAmount,
                term,
                monthlyPayment,
                rate,
                true,true
        ));

        return result;
    }

}
