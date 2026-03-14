package app.gozenko.service;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.PaymentScheduleElementDto;
import app.gozenko.dto.ScoringDataDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalcCreditValueService {
    private Integer term;
    private BigDecimal amount;

    public BigDecimal calcMonthlyPayment(BigDecimal totalRate, BigDecimal totalAmount, Integer term){
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

    //основная логика формирования кредитного предложения
    public CreditDto mainCounting(ScoringDataDto request, BigDecimal rate){
        this.amount = request.getAmount();
        this.term = request.getTerm();

        BigDecimal monthlyPayment = calcMonthlyPayment(
                rate, amount, term
        );

        List<PaymentScheduleElementDto> schedule = createPaymentSchedule(
                amount, term, rate, monthlyPayment);

        BigDecimal psk = calcPsk(amount, schedule, term);

        return CreditDto.builder()
                .amount(amount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .psk(psk)
                .isInsuranceEnabled(request.getIsInsuranceEnabled())
                .isSalaryClient(request.getIsSalaryClient())
                .paymentSchedule(schedule)
                .build();
    }

    public BigDecimal calcPsk(
            BigDecimal loanAmount,
            List<PaymentScheduleElementDto> schedule,
            Integer termMonths
    ) {
        // Общая сумма всех платежей по графику
        BigDecimal totalPayments = schedule.stream()
                .map(PaymentScheduleElementDto::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Общая переплата
        BigDecimal overpayment = totalPayments.subtract(loanAmount);

        // Средний срок кредита в днях
        BigDecimal avgTermDays = BigDecimal.valueOf(termMonths)
                .multiply(BigDecimal.valueOf(365.0 / 12 / 2)); // term * (365/12/2)

        // ПСК = (переплата / сумма_выдачи) * (365 / средний_срок) * 100
        BigDecimal psk = overpayment
                .divide(loanAmount, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(365))
                .divide(avgTermDays, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return psk.setScale(2, RoundingMode.HALF_UP);
    }

    public List<PaymentScheduleElementDto> createPaymentSchedule(
            BigDecimal loanAmount,
            Integer termMonths,
            BigDecimal annualRate,
            BigDecimal monthlyPayment
    ) {
        List<PaymentScheduleElementDto> schedule = new ArrayList<>();

        // Месячная ставка в долях
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        LocalDate currentDate = LocalDate.now();
        BigDecimal remainingDebt = loanAmount;

        for (int i = 1; i <= termMonths; i++) {
            // Расчет процентов за текущий месяц
            BigDecimal interestPayment = remainingDebt
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            // Расчет погашения основного долга
            BigDecimal debtPayment = monthlyPayment
                    .subtract(interestPayment)
                    .setScale(2, RoundingMode.HALF_UP);

            // Для последнего месяца корректируем, чтобы остаток сошелся
            if (i == termMonths) {
                debtPayment = remainingDebt;
                monthlyPayment = interestPayment.add(debtPayment);
            }

            // Здесь уменьшаем остаток долга
            remainingDebt = remainingDebt
                    .subtract(debtPayment)
                    .setScale(2, RoundingMode.HALF_UP);

            PaymentScheduleElementDto element = PaymentScheduleElementDto.builder()
                    .number(i)
                    .date(currentDate.plusMonths(i))
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt.compareTo(BigDecimal.ZERO) < 0
                            ? BigDecimal.ZERO : remainingDebt)
                    .build();

            schedule.add(element);

            if (remainingDebt.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }

        return schedule;
    }
}
