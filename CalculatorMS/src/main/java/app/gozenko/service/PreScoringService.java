package app.gozenko.service;

import app.gozenko.dto.LoanStatementRequestDto;
import static app.gozenko.validation.ValidateData.*;
import org.springframework.stereotype.Service;

@Service
public class PreScoringService {


    public void preScoring(LoanStatementRequestDto request){
        if (!(request.getFirstName().matches(validName)))
            throw new IllegalArgumentException("Имя должно быть от 2 до 30 символов");

        if (!(request.getMiddleName().matches(validName)))
            throw new IllegalArgumentException("Отчество должно быть от 2 до 30 символов");

        if (!(request.getLastName().matches(validName)))
            throw new IllegalArgumentException("Фамилия должна быть от 2 до 30 символов");

        if (!(request.getAmount().compareTo(validAmount) >= 0))
            throw new IllegalArgumentException("Сумма должно быть больше 20000");

        if (!(request.getTerm() >= validTerm))
            throw new IllegalArgumentException("Срок должно быть не меньше 6");

        if (!(request.getEmail().matches(validEmail)))
            throw new IllegalArgumentException("Некорреткный формат почты");

        if (!(request.getBirthday().toString().matches(validBirthday)))
            throw new IllegalArgumentException("Форматы даты должен быть гггг-мм-дд");

        if (!(checkLegalAge(request.getBirthday())))
            throw new IllegalArgumentException("Возраст должен быть от 18");

        if (!(request.getPassportSeries().matches(validPassportSeries)))
            throw new IllegalArgumentException("Серия паспорта должна быть 4 символа");

        if (!(request.getPassportNumber().matches(validPassportNumber)))
            throw new IllegalArgumentException("Номер паспорта должен быть 6 символов");

    }
}
