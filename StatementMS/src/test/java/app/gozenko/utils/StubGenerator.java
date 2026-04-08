package app.gozenko.utils;

import app.gozenko.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StubGenerator {

    public static LoanStatementRequestDto createLoanStatementRequestWithAge(int age) {
        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthday(LocalDate.now().minusYears(age))
                .email("ivan@example.com")
                .passportSeries("1234")
                .passportNumber("123456")
                .build();
    }

    public static LoanStatementRequestDto createLoanStatementRequestWithBirthday(LocalDate birthday) {
        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthday(birthday)
                .email("ivan@example.com")
                .passportSeries("1234")
                .passportNumber("123456")
                .build();
    }
}
