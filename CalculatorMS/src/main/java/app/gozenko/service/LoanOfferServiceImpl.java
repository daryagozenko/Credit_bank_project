package app.gozenko.service;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.interfaces.LoanOfferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LoanOfferServiceImpl implements LoanOfferService {
    @Value("${app.gozenko.base-rate}")
    private BigDecimal propertyRate;
    @Value("${app.gozenko.insurance}")
    private BigDecimal insuranceRate;
    @Value("${app.gozenko.rate-salary-client}")
    private BigDecimal rateSalaryClient;

    @Override
    public List<LoanOfferDto> createLoanOffers(BigDecimal amount, Integer term){
        List<LoanOfferDto> result = new ArrayList<>();

        result.add(isSalaryAndInsurance(amount, term));
        result.add(isInsurance(amount, term));
        result.add(isSalary(amount, term));
        result.add(noneSalaryAndInsurance(amount,term));

        log.info(result.toString());
        return result;
    }

    LoanOfferDto isSalaryAndInsurance(BigDecimal amount, Integer term){
        //Текущая ставка по кредиту
        BigDecimal totalRate = propertyRate
                .subtract(insuranceRate)
                .subtract(rateSalaryClient);

        // Расчет страховки
        BigDecimal priceInsurance = amount.multiply(
                insuranceRate.divide(BigDecimal.valueOf(100)));

        BigDecimal totalAmount = amount.add(priceInsurance);

        BigDecimal monthlyPayment = calcMonthlyPayment(totalRate, totalAmount, term);

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

    LoanOfferDto isSalary(BigDecimal amount, Integer term){
        //Текущая ставка по кредиту
        BigDecimal totalRate = propertyRate
                .subtract(rateSalaryClient);

        BigDecimal monthlyPayment = calcMonthlyPayment(totalRate, amount, term);

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

    LoanOfferDto isInsurance(BigDecimal amount, Integer term){
        //Текущая ставка по кредиту
        BigDecimal totalRate = propertyRate
                .subtract(insuranceRate);

        // Расчет страховки
        BigDecimal priceInsurance = amount.multiply(
                insuranceRate.divide(BigDecimal.valueOf(100), RoundingMode.FLOOR));

        BigDecimal totalAmount = amount.add(priceInsurance);

        BigDecimal monthlyPayment = calcMonthlyPayment(totalRate, totalAmount, term);

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

    LoanOfferDto noneSalaryAndInsurance(BigDecimal amount, Integer term){

        BigDecimal monthlyPayment = calcMonthlyPayment(propertyRate, amount, term);

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

    BigDecimal calcMonthlyPayment(BigDecimal totalRate, BigDecimal totalAmount, Integer term){
        // Расчет месячной ставки
        BigDecimal monthlyRate = totalRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        // Расчет (1 + monthlyRate)^term
        BigDecimal one = BigDecimal.ONE;
        BigDecimal temp = one.add(monthlyRate).pow(term);

        // Расчет monthlyRate * temp / (temp - 1)
        BigDecimal tempMinusOne = temp.subtract(one);
        BigDecimal annuityCoefficient = monthlyRate
                .multiply(temp)
                .divide(tempMinusOne, 10, RoundingMode.HALF_UP);

        // Расчет totalAmount * annuityCoefficient
        BigDecimal monthlyPayment = totalAmount
                .multiply(annuityCoefficient)
                .setScale(2, RoundingMode.HALF_UP);

        return monthlyPayment;
    }

}
