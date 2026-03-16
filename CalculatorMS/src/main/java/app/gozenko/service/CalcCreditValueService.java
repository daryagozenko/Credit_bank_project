package app.gozenko.service;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.PaymentScheduleElementDto;
import app.gozenko.dto.ScoringDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CalcCreditValueService {

    public BigDecimal calcMonthlyPayment(BigDecimal totalRate, BigDecimal totalAmount, Integer term){
        log.info("Beginning calcMonthlyRate: totalRate={}, totalAmount={}, term={}",
                totalRate, totalAmount, term);
        // Расчет месячной ставки
        BigDecimal monthlyRate = totalRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        // Расчет (1 + monthlyRate)^term
        BigDecimal one = BigDecimal.ONE;
        BigDecimal temp = one.add(monthlyRate).pow(term);
        log.debug("temp={}, one={}",temp,one);

        // Расчет monthlyRate * temp / (temp - 1)
        BigDecimal tempMinusOne = temp.subtract(one);
        BigDecimal annuityCoefficient = monthlyRate
                .multiply(temp)
                .divide(tempMinusOne, 10, RoundingMode.HALF_UP);
        log.debug("annuityCoefficient={}",annuityCoefficient);

        // Расчет totalAmount * annuityCoefficient
        return totalAmount
                .multiply(annuityCoefficient)
                .setScale(2, RoundingMode.HALF_UP);
    }

    //основная логика формирования кредитного предложения
    public CreditDto mainCounting(ScoringDataDto request, BigDecimal rate){
        BigDecimal amount = request.getAmount();
        Integer term = request.getTerm();
        log.info("Beginning mainCounting: totalAmount={}, term={}",amount, term);

        BigDecimal monthlyPayment = calcMonthlyPayment(
                rate, amount, term
        );
        log.info("Result: monthlyPayment={}",monthlyPayment);

        List<PaymentScheduleElementDto> schedule = createPaymentSchedule(
                amount, term, rate, monthlyPayment);
        log.info("Result: list of schedule={}",schedule);

        BigDecimal psk = calcPsk(amount, schedule, term);
        log.info("Result: psk={}",psk);

        log.info("Creating CreditDto");
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
        log.info("Beginning calcPsk: loanAmount={}, term={}, shedule={}",loanAmount, termMonths,schedule);
        // Общая сумма всех платежей по графику
        BigDecimal totalPayments = schedule.stream()
                .map(PaymentScheduleElementDto::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Общая переплата
        BigDecimal overpayment = totalPayments.subtract(loanAmount);
        log.debug("overpayment={}",overpayment);

        // Средний срок кредита в днях
        BigDecimal avgTermDays = BigDecimal.valueOf(termMonths)
                .multiply(BigDecimal.valueOf(365.0 / 12 / 2)); // term * (365/12/2)
        log.debug("angTermsDays={}",avgTermDays);

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
        log.info("Beginning createPaymentSchedule: loanAmount={}, term={}, annualRate={}, monthlyPayment={}",
                loanAmount, termMonths,annualRate,monthlyPayment);
        List<PaymentScheduleElementDto> schedule = new ArrayList<>();

        // Месячная ставка в долях
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        LocalDate currentDate = LocalDate.now();
        BigDecimal remainingDebt = loanAmount;
        log.debug("currentDate={}, remainingDebt={}",currentDate, remainingDebt);

        for (int i = 1; i <= termMonths; i++) {
            // Расчет процентов за текущий месяц
            BigDecimal interestPayment = remainingDebt
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);
            log.debug("interestPayment={}",interestPayment);

            // Расчет погашения основного долга
            BigDecimal debtPayment = monthlyPayment
                    .subtract(interestPayment)
                    .setScale(2, RoundingMode.HALF_UP);
            log.debug("debtPayment={}",debtPayment);

            // Для последнего месяца корректируем, чтобы остаток сошелся
            if (i == termMonths) {
                debtPayment = remainingDebt;
                monthlyPayment = interestPayment.add(debtPayment);
                log.debug("If last month: debtPayment={}, monthlyPayment={}",debtPayment, monthlyPayment);
            }

            // Здесь уменьшаем остаток долга
            remainingDebt = remainingDebt
                    .subtract(debtPayment)
                    .setScale(2, RoundingMode.HALF_UP);
            log.debug("remainingDebt={}",remainingDebt);

            PaymentScheduleElementDto element = PaymentScheduleElementDto.builder()
                    .number(i)
                    .date(currentDate.plusMonths(i))
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt.compareTo(BigDecimal.ZERO) < 0
                            ? BigDecimal.ZERO : remainingDebt)
                    .build();
            log.info("Result: payment schedule element={}",element);

            schedule.add(element);

            if (remainingDebt.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }

        return schedule;
    }
}
