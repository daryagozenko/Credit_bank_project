package app.gozenko.service;

import app.gozenko.dto.LoanStatementRequestDto;

import app.gozenko.dto.ScoringDataDto;
import app.gozenko.exception.ValidationDataException;
import app.gozenko.service.interfaces.PreScoringService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@Validated
public class PreScoringServiceImpl implements PreScoringService {

    @Value("${app.gozenko.legal-age}")
    private Integer legalAge;


    @Override
    public void preScoringLoan(@Valid LoanStatementRequestDto request){
        log.debug("LoanStatement request: {}",request);

        if(!(checkLegalAge(request.getBirthday())))
            throw new ValidationDataException("Возраст должен быть больше "+legalAge);
    }

    @Override
    public void preScoringScoreData(@Valid ScoringDataDto request){
        log.debug("ScoringData request: {}",request);

        if(!(checkLegalAge(request.getBirthday())))
            throw new ValidationDataException("Возраст должен быть больше "+legalAge);

    }

    private boolean checkLegalAge(LocalDate birthday){
        try{
            LocalDate today = LocalDate.now();
            log.debug("legalAge = {}",legalAge);
            LocalDate yearsAgo = today.minusYears(legalAge);
            return !birthday.isAfter(yearsAgo);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e+" Некорректная дата");
        }
    }
}
