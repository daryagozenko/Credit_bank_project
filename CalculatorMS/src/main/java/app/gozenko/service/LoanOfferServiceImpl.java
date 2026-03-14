package app.gozenko.service;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.interfaces.LoanOfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanOfferServiceImpl implements LoanOfferService {
    @Value("${app.gozenko.base-rate}")
    private BigDecimal propertyRate;
    @Value("${app.gozenko.insurance}")
    private BigDecimal insuranceRate;
    @Value("${app.gozenko.rate-salary-client}")
    private BigDecimal rateSalaryClient;

    private final CheckValueService checkValueService;

    @Override
    public List<LoanOfferDto> createLoanOffers(BigDecimal amount, Integer term){
        List<LoanOfferDto> result = new ArrayList<>();

        result.add(checkValueService.isSalaryAndInsurance(amount, term));
        result.add(checkValueService.isInsurance(amount, term));
        result.add(checkValueService.isSalary(amount, term));
        result.add(checkValueService.noneSalaryAndInsurance(amount,term));

        log.info(result.toString());
        return result;
    }



}
