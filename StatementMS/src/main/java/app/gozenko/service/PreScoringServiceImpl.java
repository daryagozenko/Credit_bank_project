package app.gozenko.service;

import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.exception.ValidationDataException;
import app.gozenko.service.interfaces.PreScoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Slf4j
@Service
@Validated
public class PreScoringServiceImpl implements PreScoringService {

    private final Integer legalAge;

    @Autowired
    public PreScoringServiceImpl(@Value("${app.gozenko.legal-age}") Integer legalAge) {
        this.legalAge = legalAge;
    }

    @Override
    public void preScoringLoan(LoanStatementRequestDto request) {
        log.debug("LoanStatement request: {}", request);

        if (!(checkLegalAge(request.getBirthday())))
            throw new ValidationDataException("Возраст должен быть больше " + legalAge);
    }

    private boolean checkLegalAge(LocalDate birthday) {
        LocalDate today = LocalDate.now();
        log.debug("legalAge = {}", legalAge);
        LocalDate yearsAgo = today.minusYears(legalAge);
        return !birthday.isAfter(yearsAgo);
    }
}
