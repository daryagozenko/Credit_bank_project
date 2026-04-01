package app.gozenko.service;

import app.gozenko.dto.LoanOfferDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckValueService {

    private static final BigDecimal BASE_PERCENT = BigDecimal.valueOf(100);

    private final CalcCreditValueService calcCreditValueService;

    private BigDecimal baseRate;
    private BigDecimal insuranceRate;
    private BigDecimal rateSalaryClient;

    public CheckValueService(CalcCreditValueService calcCreditValueService,
                             @Value("${app.gozenko.base-rate}") BigDecimal baseRate,
                             @Value("${app.gozenko.insurance}") BigDecimal insuranceRate,
                             @Value("${app.gozenko.rate-salary-client}") BigDecimal rateSalaryClient) {
        this.calcCreditValueService = calcCreditValueService;
        this.baseRate = baseRate;
        this.insuranceRate = insuranceRate;
        this.rateSalaryClient = rateSalaryClient;
    }


    public LoanOfferDto createSalaryAndInsuranceLoanOffer(BigDecimal amount, Integer term) {
        BigDecimal currentCreditRate = baseRate
                .subtract(insuranceRate)
                .subtract(rateSalaryClient);
        log.debug("createSalaryAndInsuranceLoanOffer: currentCreditRate-{}", currentCreditRate);

        BigDecimal priceOfInsurance = amount.multiply(
                insuranceRate.divide(BASE_PERCENT, RoundingMode.FLOOR));
        log.debug("createSalaryAndInsuranceLoanOffer: priceOfInsurance-{}", priceOfInsurance);

        BigDecimal totalAmount = amount.add(priceOfInsurance);
        log.debug("createSalaryAndInsuranceLoanOffer: totalAmount-{}", totalAmount);

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(currentCreditRate, totalAmount, term);
        log.info("Result in createSalaryAndInsuranceLoanOffer: monthlyPayment-{}", monthlyPayment);

        return buildNewLoanOfferDto(amount, totalAmount, term, monthlyPayment,
                currentCreditRate, true, true);
    }

    public LoanOfferDto createSalaryLoanOffer(BigDecimal amount, Integer term) {
        BigDecimal currentCreditRate = baseRate
                .subtract(rateSalaryClient);
        log.debug("createSalaryLoanOffer: currentCreditRate-{}", currentCreditRate);

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(currentCreditRate, amount, term);
        log.info("Result in createSalaryLoanOffer: monthlyPayment-{}", monthlyPayment);

        return buildNewLoanOfferDto(amount, amount, term, monthlyPayment,
                currentCreditRate, false, true);
    }

    public LoanOfferDto createInsuranceLoanOffer(BigDecimal amount, Integer term) {
        BigDecimal currentCreditRate = baseRate
                .subtract(insuranceRate);
        log.debug("createInsuranceLoanOffer: currentCreditRate-{}", currentCreditRate);

        BigDecimal priceOfInsurance = amount.multiply(
                insuranceRate.divide(BASE_PERCENT, RoundingMode.FLOOR));
        log.debug("createInsuranceLoanOffer: priceOfInsurance-{}", priceOfInsurance);

        BigDecimal totalAmount = amount.add(priceOfInsurance);
        log.debug("createInsuranceLoanOffer: totalAmount-{}", totalAmount);

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(currentCreditRate, totalAmount, term);
        log.info("Result in createInsuranceLoanOffer: monthlyPayment-{}", monthlyPayment);

        return buildNewLoanOfferDto(amount, totalAmount, term, monthlyPayment,
                currentCreditRate, true, false);
    }

    public LoanOfferDto createDefaultLoanOffer(BigDecimal amount, Integer term) {

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(baseRate, amount, term);
        log.info("Result in createDefaultLoanOffer: monthlyPayment-{}", monthlyPayment);

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