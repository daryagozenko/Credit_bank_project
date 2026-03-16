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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService {

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

    @PostConstruct
    public void init(){
        log.info("Values from properties: rateToManager-{}, rateToTopManager{}, rateMarried-{}",
                rateToManager, rateToTopManager, rateMarried);
        log.info("Values from properties: rateDivorced-{}, rateWithAge{}, rateWithDependent-{}",
                rateDivorced, rateWithAge, rateWithDependent);
    }


    @Override
    public CreditDto createScoringData(ScoringDataDto request) {
        String message = hasConstraint(request);
        if (message != null) {
            throw new UnScoringDataException(message);
        }
        initialValues(request);
        updateData(request);

        log.info("Start to main counting");
        return calcCreditValueService.mainCounting(request, rate);
    }


    private String hasConstraint(ScoringDataDto request) {
        if (request.getEmployment().getEmploymentStatus().equals(EmploymentStatus.NOT_WORK))
            return "Безработный";
        if (request.getAmount().compareTo(request.getEmployment().getSalary().multiply(BigDecimal.valueOf(24))) > 0)
            return "Сумма займа больше, чем 24 зарплаты";

        int age = calculateAge(request.getBirthday());
        if (age < 20) return "Моложе 20";
        if (age > 65) return "Старше 65";

        int workExpTotal = request.getEmployment().getWorkExperienceTotal();
        if (workExpTotal < 12) return "Общий стаж работы менее 12 месяцев";
        int workExpCurr = request.getEmployment().getWorkExperienceCurrent();
        if (workExpCurr < 3) return "Текущий стаж работы менее 3 месяцев";

        return null;
    }

    //тут основная логика прескоринга
    private void updateData(ScoringDataDto request) {
        EmploymentDto employmentDto = request.getEmployment();
        log.info("Beginning: rate={}",rate);

        if (employmentDto.getPosition().equals(Position.MANAGER)) {
            rate = rate.subtract(rateToManager);
            log.debug("rate subtract rateToManager={}",rate);
        }
        if (employmentDto.getPosition().equals(Position.TOP_MANAGER)) {
            rate = rate.subtract(rateToTopManager);
            log.debug("rate subtract rateToTopManager={}",rate);
        }

        if (request.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            rate = rate.subtract(rateMarried);
            log.debug("rate subtract rateToMarried={}",rate);
        }
        if (request.getMaritalStatus().equals(MaritalStatus.DIVORCED)) {
            rate = rate.add(rateDivorced);
            log.debug("rate add rateDivorced={}",rate);
        }

        int age = calculateAge(request.getBirthday());
        if (request.getGender().equals(Gender.FEMALE) && (age > 31 && age < 61)) {
            rate = rate.subtract(rateWithAge);
            log.debug("rate subtract rateWithAge to Female={}",rate);
        }
        if (request.getGender().equals(Gender.MALE) && (age > 29 && age < 56)) {
            rate = rate.subtract(rateWithAge);
            log.debug("rate subtract rateWithAge to Male={}",rate);
        }

        if (request.getDependentAmount() > 2) {
            rate = rate.subtract(rateWithDependent);
            log.debug("rate subtract rateWithDependent={}",rate);
        }

    }

    private void initialValues(ScoringDataDto request) {
        Integer term = request.getTerm();
        BigDecimal amount = request.getAmount();
        log.info("Beginning amount={} and term={}",amount,term);

        if (request.getIsSalaryClient() && request.getIsInsuranceEnabled()) {
            rate = checkValueService.salaryAndInsuranceClient(amount, term).getRate();
            log.debug("rate with salary client and insurance={}",rate);
        }

        else if (request.getIsInsuranceEnabled()) {
            rate = checkValueService.insuranceClient(amount, term).getRate();
            log.debug("rate with insurance={}",rate);
        }

        else if (request.getIsSalaryClient()) {
            rate = checkValueService.salaryClient(amount, term).getRate();
            log.debug("rate with salary client={}",rate);
        }

        else {
            rate = checkValueService.noneSalaryAndInsuranceClient(amount, term).getRate();
            log.debug("rate with none salary client and insurance={}",rate);
        }
    }

    private int calculateAge(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}
