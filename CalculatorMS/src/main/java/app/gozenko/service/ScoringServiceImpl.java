package app.gozenko.service;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.EmploymentDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.enums.EmploymentStatus;
import app.gozenko.enums.Gender;
import app.gozenko.enums.MaritalStatus;
import app.gozenko.enums.Position;
import app.gozenko.exception.UnScoringDataException;
import app.gozenko.service.interfaces.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService {

    private static final Integer REQUIRED_TOTAL_WORK_EXPERIENCE = 3;
    private static final Integer REQUIRED_COMMON_WORK_EXPERIENCE = 12;
    private static final Integer MAX_AMOUNT_DIFF_SALARY = 24;
    private static final Integer MIN_AGE = 20;
    private static final Integer MAX_AGE = 65;
    private static final Integer MIN_AGE_FEMALE = 31;
    private static final Integer MAX_AGE_FEMALE = 61;
    private static final Integer MIN_AGE_MALE = 29;
    private static final Integer MAX_AGE_MALE = 56;
    private static final Integer TOTAL_DEPENDENT = 2;

    private final CheckValueService checkValueService;
    private final CalcCreditValueService calcCreditValueService;

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

    private BigDecimal rate;


    @Override
    public CreditDto createScoringData(ScoringDataDto request) {
        Optional<String> message = checkConstraint(request);
        if (message.isPresent()) {
            throw new UnScoringDataException(message.get());
        }
        initialValues(request);
        updateData(request);

        log.info("Start to main counting");
        return calcCreditValueService.mainCounting(request, rate);
    }


    private Optional<String> checkConstraint(ScoringDataDto request) {
        if (request.getEmployment().getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED))
            return Optional.of("Безработный");
        if (request.getAmount().compareTo(request.getEmployment()
                .getSalary().multiply(BigDecimal.valueOf(MAX_AMOUNT_DIFF_SALARY))) > 0)
            return Optional.of(String.format("Сумма займа больше, чем %d зарплаты", MAX_AMOUNT_DIFF_SALARY));

        int age = calculateAge(request.getBirthday());
        if (age < MIN_AGE) return Optional.of(String.format("Моложе %d", MIN_AGE));
        if (age > MAX_AGE) return Optional.of(String.format("Старше %d", MAX_AGE));

        int workExpTotal = request.getEmployment().getWorkExperienceTotal();
        if (workExpTotal < REQUIRED_COMMON_WORK_EXPERIENCE) return Optional.of(
                String.format("Общий стаж работы менее %d месяцев", REQUIRED_COMMON_WORK_EXPERIENCE));
        int workExpCurr = request.getEmployment().getWorkExperienceCurrent();
        if (workExpCurr < REQUIRED_TOTAL_WORK_EXPERIENCE) return Optional.of(
                String.format("Текущий стаж работы менее %d месяцев", REQUIRED_TOTAL_WORK_EXPERIENCE));

        return Optional.empty();
    }

    //тут основная логика прескоринга
    private void updateData(ScoringDataDto request) {
        EmploymentDto employmentDto = request.getEmployment();
        log.info("Beginning: rate={}", rate);

        if (employmentDto.getPosition().equals(Position.MANAGER)) {
            rate = rate.subtract(rateToManager);
            log.debug("rate subtract rateToManager={}", rate);
        }
        if (employmentDto.getPosition().equals(Position.TOP_MANAGER)) {
            rate = rate.subtract(rateToTopManager);
            log.debug("rate subtract rateToTopManager={}", rate);
        }

        if (request.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            rate = rate.subtract(rateMarried);
            log.debug("rate subtract rateToMarried={}", rate);
        }
        if (request.getMaritalStatus().equals(MaritalStatus.DIVORCED)) {
            rate = rate.add(rateDivorced);
            log.debug("rate add rateDivorced={}", rate);
        }

        int age = calculateAge(request.getBirthday());
        if (request.getGender().equals(Gender.FEMALE) && (age > MIN_AGE_FEMALE && age < MAX_AGE_FEMALE)) {
            rate = rate.subtract(rateWithAge);
            log.debug("rate subtract rateWithAge to Female={}", rate);
        }
        if (request.getGender().equals(Gender.MALE) && (age > MIN_AGE_MALE && age < MAX_AGE_MALE)) {
            rate = rate.subtract(rateWithAge);
            log.debug("rate subtract rateWithAge to Male={}", rate);
        }

        if (request.getDependentAmount() > TOTAL_DEPENDENT) {
            rate = rate.subtract(rateWithDependent);
            log.debug("rate subtract rateWithDependent={}", rate);
        }

    }

    private void initialValues(ScoringDataDto request) {
        Integer term = request.getTerm();
        BigDecimal amount = request.getAmount();
        log.info("Beginning amount={} and term={}", amount, term);

        if (request.getIsSalaryClient() && request.getIsInsuranceEnabled()) {
            rate = checkValueService.createSalaryAndInsuranceLoanOffer(amount, term).getRate();
            log.debug("rate with salary client and insurance={}", rate);
        } else if (request.getIsInsuranceEnabled()) {
            rate = checkValueService.createInsuranceLoanOffer(amount, term).getRate();
            log.debug("rate with insurance={}", rate);
        } else if (request.getIsSalaryClient()) {
            rate = checkValueService.createSalaryLoanOffer(amount, term).getRate();
            log.debug("rate with salary client={}", rate);
        } else {
            rate = checkValueService.createDefaultLoanOffer(amount, term).getRate();
            log.debug("rate with none salary client and insurance={}", rate);
        }
    }

    private int calculateAge(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}
