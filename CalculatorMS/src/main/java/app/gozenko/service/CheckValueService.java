package app.gozenko.service;

import app.gozenko.dto.LoanOfferDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckValueService {

    private final CalcCreditValueService calcCreditValueService;

    @Value("${app.gozenko.base-rate}")
    private BigDecimal baseRate;
    @Value("${app.gozenko.insurance}")
    private BigDecimal insuranceRate;
    @Value("${app.gozenko.rate-salary-client}")
    private BigDecimal rateSalaryClient;

    @PostConstruct
    public void init(){
        log.info("Values from properties: baseRate-{}, insuranсeRate{}, rateSalaryClient-{}",
                baseRate, insuranceRate, rateSalaryClient);
    }

    public LoanOfferDto salaryAndInsuranceClient(BigDecimal amount, Integer term) {
        BigDecimal currentCreditRate = baseRate
                .subtract(insuranceRate)
                .subtract(rateSalaryClient);
        log.debug("salaryAndInsuranceClient: currentCreditRate-{}",currentCreditRate);

        BigDecimal priceOfInsurance = amount.multiply(
                insuranceRate.divide(BigDecimal.valueOf(100)));
        log.debug("salaryAndInsuranceClient: priceOfInsurance-{}",priceOfInsurance);

        BigDecimal totalAmount = amount.add(priceOfInsurance);
        log.debug("salaryAndInsuranceClient: totalAmount-{}",totalAmount);

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(currentCreditRate, totalAmount, term);
        log.info("Result in salaryAndInsuranceClient: monthlyPayment-{}",monthlyPayment);

        return buildNewLoanOfferDto(amount, totalAmount, term, monthlyPayment,
                currentCreditRate, true, true);
    }

    public LoanOfferDto salaryClient(BigDecimal amount, Integer term) {
        BigDecimal currentCreditRate = baseRate
                .subtract(rateSalaryClient);
        log.debug("salaryClient: currentCreditRate-{}",currentCreditRate);

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(currentCreditRate, amount, term);
        log.info("Result in salaryClient: monthlyPayment-{}",monthlyPayment);

        return buildNewLoanOfferDto(amount, amount, term, monthlyPayment,
                currentCreditRate, false, true);
    }

    public LoanOfferDto insuranceClient(BigDecimal amount, Integer term) {
        BigDecimal currentCreditRate = baseRate
                .subtract(insuranceRate);
        log.debug("insuranceClient: currentCreditRate-{}",currentCreditRate);

        BigDecimal priceOfInsurance = amount.multiply(
                insuranceRate.divide(BigDecimal.valueOf(100), RoundingMode.FLOOR));
        log.debug("insuranceClient: priceOfInsurance-{}",priceOfInsurance);

        BigDecimal totalAmount = amount.add(priceOfInsurance);
        log.debug("insuranceClient: totalAmount-{}",totalAmount);

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(currentCreditRate, totalAmount, term);
        log.info("Result in insuranceClient: monthlyPayment-{}",monthlyPayment);

        return buildNewLoanOfferDto(amount, totalAmount, term, monthlyPayment,
                currentCreditRate, true, false);
    }

    public LoanOfferDto noneSalaryAndInsuranceClient(BigDecimal amount, Integer term) {

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(baseRate, amount, term);
        log.info("Result in noneSalaryAndInsuranceClient: monthlyPayment-{}",monthlyPayment);

        return buildNewLoanOfferDto(amount, amount, term, monthlyPayment,
                baseRate, false, false);
    }

    private LoanOfferDto buildNewLoanOfferDto(BigDecimal requestedAmount,
                                              BigDecimal totalAmount,
                                              Integer term,
                                              BigDecimal monthlyPayment,
                                              BigDecimal rate,
                                              Boolean isInsuranceEnabled,
                                              Boolean isSalaryClient) {
        log.info("Build new LoanOfferDto");
        return LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(requestedAmount)
                .totalAmount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }
}
