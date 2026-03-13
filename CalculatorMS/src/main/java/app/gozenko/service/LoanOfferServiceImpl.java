package app.gozenko.service;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.interfaces.LoanOfferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LoanOfferServiceImpl implements LoanOfferService {
    @Value("${app.gozenko.base-rate}")
    private String propertyRate;

    @Override
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
        log.info(result.toString());
        return result;
    }

}
