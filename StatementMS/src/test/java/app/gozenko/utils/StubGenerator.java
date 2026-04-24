package app.gozenko.utils;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    public static LoanStatementRequestDto createEmptyLoanStatementRequest() {
        return LoanStatementRequestDto.builder().build();
    }

    public static LoanOfferDto createEmptyLoanOfferDto() {
        return LoanOfferDto.builder().build();
    }

    public static LoanStatementRequestDto createValidLoanStatementRequest() {
        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal("1000000"))
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthday(LocalDate.now().minusYears(30))
                .email("ivan@example.com")
                .passportSeries("1234")
                .passportNumber("567890")
                .build();
    }

    public static LoanOfferDto createValidLoanOffer() {
        return LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(new BigDecimal("1000000"))
                .totalAmount(new BigDecimal("1000000"))
                .term(12)
                .monthlyPayment(new BigDecimal("34000"))
                .rate(new BigDecimal("15"))
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .build();
    }

    public static List<LoanOfferDto> createValidOffers() {
        LoanOfferDto loanOffer = createEmptyLoanOfferDto();

        return List.of(loanOffer, loanOffer, loanOffer, loanOffer);
    }
}
