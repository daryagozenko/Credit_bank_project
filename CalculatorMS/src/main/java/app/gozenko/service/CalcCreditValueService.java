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

    private static final BigDecimal BASE_PERCENT = BigDecimal.valueOf(100);
    private static final Integer MIN_SCALE = 2;
    private static final Integer BASE_SCALE = 10;
    private static final Integer MONTHS = 12;
    private static final Integer DAYS = 365;
    private static final Integer AVERAGE = 2;


    /**
     * Calculating monthly payment
     * <p>
     * Calculation of the monthlyRate = totalRate / BASE_PERCENT / MONTHS
     * Next calculating temp = (1 + monthlyRate)^term
     * Calculating annuityCoefficient = monthlyRate * temp / (temp - 1)
     * Result monthlyPayment = totalAmount * annuityCoefficient
     * </p>
     *
     * @param totalRate   current loan rate
     * @param totalAmount current loan amount
     * @param term        loan term
     * @return monthly payment
     */
    public BigDecimal calcMonthlyPayment(BigDecimal totalRate, BigDecimal totalAmount, Integer term) {
        log.info("Beginning calcMonthlyRate: totalRate={}, totalAmount={}, term={}",
                totalRate, totalAmount, term);
        BigDecimal monthlyRate = totalRate
                .divide(BASE_PERCENT, BASE_SCALE, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(MONTHS), BASE_SCALE, RoundingMode.HALF_UP);

        BigDecimal one = BigDecimal.ONE;
        BigDecimal temp = one.add(monthlyRate).pow(term);
        log.debug("temp={}, one={}", temp, one);

        BigDecimal tempMinusOne = temp.subtract(one);
        BigDecimal annuityCoefficient = monthlyRate
                .multiply(temp)
                .divide(tempMinusOne, BASE_SCALE, RoundingMode.HALF_UP);
        log.debug("annuityCoefficient={}", annuityCoefficient);

        return totalAmount
                .multiply(annuityCoefficient)
                .setScale(MIN_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * The main logic of forming a loan offer
     * <p>
     * Calculating monthlyPayment = {@link #calcMonthlyPayment(BigDecimal, BigDecimal, Integer)}
     * Calculating list of schedules = {@link #createPaymentSchedule(BigDecimal, Integer, BigDecimal, BigDecimal)}
     * Calculating psk (the full cost of the loan) = {@link #calcPsk(BigDecimal, List, Integer)}
     * </p>
     *
     * @param request scoring data to loan of credit
     * @param rate    current rate of credit
     * @return CreditDto
     */
    public CreditDto mainCounting(ScoringDataDto request, BigDecimal rate) {
        BigDecimal amount = request.getAmount();
        Integer term = request.getTerm();
        log.info("Beginning mainCounting: totalAmount={}, term={}", amount, term);

        BigDecimal monthlyPayment = calcMonthlyPayment(
                rate, amount, term
        );
        log.info("Result: monthlyPayment={}", monthlyPayment);

        List<PaymentScheduleElementDto> schedule = createPaymentSchedule(
                amount, term, rate, monthlyPayment);
        log.info("Result: list of schedule={}", schedule);

        BigDecimal psk = calcPsk(amount, schedule, term);
        log.info("Result: psk={}", psk);

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

    /**
     * Calculating full cost of the loan
     * <p>
     * Calculating totalPayments =  sum of all payments according to the schedule
     * Calculating overpayment = totalPayments - loanAmount
     * Calculating avgTermDays = term * (DAYS / MONTHS / AVERAGE)
     * Calculating psk = (overpayment / loanAmount) * (DAYS / avgTermDays) * BASE_PERCENT
     * </p>
     *
     * @param loanAmount current amount of loan
     * @param schedule   full schedule of payments
     * @param termMonths number of months
     * @return full cost of the loan (psk)
     */
    public BigDecimal calcPsk(
            BigDecimal loanAmount,
            List<PaymentScheduleElementDto> schedule,
            Integer termMonths
    ) {
        log.info("Beginning calcPsk: loanAmount={}, term={}, shedule={}", loanAmount, termMonths, schedule);
        BigDecimal totalPayments = schedule.stream()
                .map(PaymentScheduleElementDto::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overpayment = totalPayments.subtract(loanAmount);
        log.debug("overpayment={}", overpayment);

        BigDecimal avgTermDays = BigDecimal.valueOf(termMonths)
                .multiply(BigDecimal.valueOf(DAYS / MONTHS / AVERAGE)); // term * (365/12/2)
        log.debug("angTermsDays={}", avgTermDays);

        BigDecimal psk = overpayment
                .divide(loanAmount, BASE_SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(DAYS))
                .divide(avgTermDays, BASE_SCALE, RoundingMode.HALF_UP)
                .multiply(BASE_PERCENT);

        return psk.setScale(MIN_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Creating a payment schedule for the loan
     * <p>
     * Calculating monthlyRate = annualRate / BASE_PERCENT / MONTHS
     * For each month in the term:
     * Calculating interestPayment = remainingDebt * monthlyRate
     * Calculating debtPayment = monthlyPayment - interestPayment
     * For the last month: adjusting debtPayment to equal remainingDebt
     * Calculating remainingDebt = remainingDebt - debtPayment
     * Creating list of payment schedule with payment details
     * </p>
     *
     * @param loanAmount     current amount of loan
     * @param termMonths     loan term in months
     * @param annualRate     current annual loan rate
     * @param monthlyPayment calculated monthly payment amount
     * @return list of payment schedule elements for each month
     */
    public List<PaymentScheduleElementDto> createPaymentSchedule(
            BigDecimal loanAmount,
            Integer termMonths,
            BigDecimal annualRate,
            BigDecimal monthlyPayment
    ) {
        log.info("Beginning createPaymentSchedule: loanAmount={}, term={}, annualRate={}, monthlyPayment={}",
                loanAmount, termMonths, annualRate, monthlyPayment);
        List<PaymentScheduleElementDto> schedule = new ArrayList<>();

        BigDecimal monthlyRate = annualRate
                .divide(BASE_PERCENT, BASE_SCALE, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(MONTHS), BASE_SCALE, RoundingMode.HALF_UP);

        LocalDate currentDate = LocalDate.now();
        BigDecimal remainingDebt = loanAmount;
        log.debug("currentDate={}, remainingDebt={}", currentDate, remainingDebt);

        for (int i = 1; i <= termMonths; i++) {
            BigDecimal interestPayment = remainingDebt
                    .multiply(monthlyRate)
                    .setScale(MIN_SCALE, RoundingMode.HALF_UP);
            log.debug("interestPayment={}", interestPayment);

            BigDecimal debtPayment = monthlyPayment
                    .subtract(interestPayment)
                    .setScale(MIN_SCALE, RoundingMode.HALF_UP);
            log.debug("debtPayment={}", debtPayment);

            if (i == termMonths) {
                debtPayment = remainingDebt;
                monthlyPayment = interestPayment.add(debtPayment);
                log.debug("If last month: debtPayment={}, monthlyPayment={}", debtPayment, monthlyPayment);
            }

            remainingDebt = remainingDebt
                    .subtract(debtPayment)
                    .setScale(MIN_SCALE, RoundingMode.HALF_UP);
            log.debug("remainingDebt={}", remainingDebt);

            PaymentScheduleElementDto element = PaymentScheduleElementDto.builder()
                    .number(i)
                    .date(currentDate.plusMonths(i))
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt.compareTo(BigDecimal.ZERO) < 0
                            ? BigDecimal.ZERO : remainingDebt)
                    .build();
            log.info("Result: payment schedule element={}", element);

            schedule.add(element);

            if (remainingDebt.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }

        return schedule;
    }
}
