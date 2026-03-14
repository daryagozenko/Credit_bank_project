package app.gozenko.service;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.EmploymentDto;
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
import java.time.LocalDate;
import java.time.Period;

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

    private final CheckValueService checkValueService;
    private final CalcCreditValueService calcCreditValueService;

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

        return calcCreditValueService.mainCounting(request, rate);
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
            rate = checkValueService.isSalaryAndInsurance(amount,term).getRate();

        else if(req.getIsInsuranceEnabled())
            rate = checkValueService.isInsurance(amount,term).getRate();

        else if(req.getIsSalaryClient())
            rate = checkValueService.isSalary(amount, term).getRate();

        else
            rate = checkValueService.noneSalaryAndInsurance(amount, term).getRate();
    }

    private int calculateAge(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}
