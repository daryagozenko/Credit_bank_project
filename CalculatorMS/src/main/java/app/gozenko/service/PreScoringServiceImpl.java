package app.gozenko.service;

import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.exception.DateParseException;
import app.gozenko.exception.ValidationDataException;
import app.gozenko.service.interfaces.PreScoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Slf4j
@Service
@Validated
public class PreScoringServiceImpl implements PreScoringService {

    @Value("${app.gozenko.legal-age}")
    private Integer legalAge;


    @Override
    public void preScoringLoan(LoanStatementRequestDto request) {
        log.debug("LoanStatement request: {}", request);

        if (!request.getBirthday().toString().matches("yyyy-MM-dd")){
            throw new DateParseException("Дата должна соответствовать формату yyyy-mm-dd");
        }

        if (!(checkLegalAge(request.getBirthday())))
            throw new ValidationDataException("Возраст должен быть больше " + legalAge);
    }

    @Override
    public void preScoringScoreData(ScoringDataDto request) {
        log.debug("ScoringData request: {}", request);

        if (!request.getBirthday().toString().matches("yyyy-MM-dd")){
            throw new DateParseException("Дата должна соответствовать формату yyyy-mm-dd");
        }

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
