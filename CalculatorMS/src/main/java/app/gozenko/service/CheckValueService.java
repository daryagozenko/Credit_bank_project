package app.gozenko.service;

import app.gozenko.dto.LoanOfferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CheckValueService {

    @Value("${app.gozenko.base-rate}")
    private BigDecimal propertyRate;
    @Value("${app.gozenko.insurance}")
    private BigDecimal insuranceRate;
    @Value("${app.gozenko.rate-salary-client}")
    private BigDecimal rateSalaryClient;

    private final CalcCreditValueService calcCreditValueService;

    public LoanOfferDto isSalaryAndInsurance(BigDecimal amount, Integer term){
        //Текущая ставка по кредиту
        BigDecimal totalRate = propertyRate
                .subtract(insuranceRate)
                .subtract(rateSalaryClient);

        // Расчет страховки
        BigDecimal priceInsurance = amount.multiply(
                insuranceRate.divide(BigDecimal.valueOf(100)));

        BigDecimal totalAmount = amount.add(priceInsurance);

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(totalRate, totalAmount, term);

        return LoanOfferDto.builderWithNewId()
                .requestedAmount(amount)
                .totalAmount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(totalRate)
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

    }

    public LoanOfferDto isSalary(BigDecimal amount, Integer term){
        //Текущая ставка по кредиту
        BigDecimal totalRate = propertyRate
                .subtract(rateSalaryClient);

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(totalRate, amount, term);

        return LoanOfferDto.builderWithNewId()
                .requestedAmount(amount)
                .totalAmount(amount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(totalRate)
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .build();
    }

    public LoanOfferDto isInsurance(BigDecimal amount, Integer term){
        //Текущая ставка по кредиту
        BigDecimal totalRate = propertyRate
                .subtract(insuranceRate);

        // Расчет страховки
        BigDecimal priceInsurance = amount.multiply(
                insuranceRate.divide(BigDecimal.valueOf(100), RoundingMode.FLOOR));

        BigDecimal totalAmount = amount.add(priceInsurance);

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(totalRate, totalAmount, term);

        return LoanOfferDto.builderWithNewId()
                .requestedAmount(amount)
                .totalAmount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(totalRate)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

    }

    public LoanOfferDto noneSalaryAndInsurance(BigDecimal amount, Integer term){

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(propertyRate, amount, term);

        return LoanOfferDto.builderWithNewId()
                .requestedAmount(amount)
                .totalAmount(amount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(propertyRate)
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

    }

}
