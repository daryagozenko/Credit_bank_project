package app.gozenko.service;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.EmploymentDto;
import app.gozenko.dto.PaymentScheduleElementDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.enums.EmploymentStatus;
import app.gozenko.enums.Gender;
import app.gozenko.enums.MaritalStatus;
import app.gozenko.enums.Position;
import app.gozenko.exception.UnScoringDataException;
import app.gozenko.interfaces.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService {

    @Value("${app.gozenko.rate-manager}")
    private BigDecimal rateToManager;
    @Value("${app.gozenko.rate-topmanager}")
    private BigDecimal rateToTopManager;
    @Value("${app.gozenko.rate-married}")
    private BigDecimal rateMarried;
    @Value("${app.gozenko.rate-divorced}")
    private BigDecimal rateDivorced;
    @Value("${app.gozenko.rate-with-age}")
    private BigDecimal rateWithAge;
    @Value("${app.gozenko.rate-with-dependent}")
    private BigDecimal rateWithDependent;

    private final LoanOfferServiceImpl loanOfferService;

    private BigDecimal rate;
    private Integer term;
    private BigDecimal amount;


    @Override
    public CreditDto createScoringData(ScoringDataDto request){
        String message = hasConstraint(request);
        if(message != null){
            throw new UnScoringDataException(message);
        }
        setBeginningValues(request);
        updateData(request);

        return mainCounting(request);
    }

    //основная логика формирования кредитного предложения
    private CreditDto mainCounting(ScoringDataDto request){
        BigDecimal monthlyPayment = loanOfferService.calcMonthlyPayment(
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

    private BigDecimal calcPsk(
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

    private List<PaymentScheduleElementDto> createPaymentSchedule(
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

    private String hasConstraint(ScoringDataDto req){
        if(req.getEmployment().getEmploymentStatus().equals(EmploymentStatus.NOT_WORK))
            return "Безработный";
        if(req.getAmount().compareTo(req.getEmployment().getSalary().multiply(BigDecimal.valueOf(24))) > 0)
            return "Сумма займа больше, чем 24 зарплаты";

        int age = calculateAge(req.getBirthday());
        if(age < 20) return "Моложе 20";
        if(age > 65) return "Старше 65";

        int workExpTotal = req.getEmployment().getWorkExperienceTotal();
        if(workExpTotal < 12) return "Общий стаж работы менее 12 месяцев";
        int workExpCurr = req.getEmployment().getWorkExperienceCurrent();
        if(workExpCurr < 3) return "Текущий стаж работы менее 3 месяцев";

        return null;
    }

    //тут основная логика прескоринга
    private void updateData(ScoringDataDto req){
        EmploymentDto empl = req.getEmployment();

        if(empl.getPosition().equals(Position.MANAGER))
            rate = rate.subtract(rateToManager);
        if(empl.getPosition().equals(Position.TOP_MANAGER))
            rate = rate.subtract(rateToTopManager);

        if(req.getMaritalStatus().equals(MaritalStatus.MARRIED))
            rate = rate.subtract(rateMarried);
        if(req.getMaritalStatus().equals(MaritalStatus.DIVORCED))
            rate = rate.add(rateDivorced);

        int age = calculateAge(req.getBirthday());
        if(req.getGender().equals(Gender.FEMALE) && (age > 31 && age < 61))
            rate = rate.subtract(rateWithAge);
        if(req.getGender().equals(Gender.MALE) && (age > 29 && age < 56))
            rate = rate.subtract(rateWithAge);

        if(req.getDependentAmount() > 2)
            rate = rate.subtract(rateWithDependent);

    }

    private void setBeginningValues(ScoringDataDto req){
        this.term = req.getTerm();
        this.amount = req.getAmount();

        if(req.getIsSalaryClient() && req.getIsInsuranceEnabled())
            rate = loanOfferService.isSalaryAndInsurance(amount,term).getRate();

        else if(req.getIsInsuranceEnabled())
            rate = loanOfferService.isInsurance(amount,term).getRate();

        else if(req.getIsSalaryClient())
            rate = loanOfferService.isSalary(amount, term).getRate();

        else
            rate = loanOfferService.noneSalaryAndInsurance(amount, term).getRate();
    }

    private int calculateAge(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}
