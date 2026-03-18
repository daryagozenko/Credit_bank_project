package app.gozenko.calculator.utils;

import app.gozenko.dto.*;
import app.gozenko.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class StubGenerator {

    public static EmploymentDto createValidEmployment() {
        return EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .salary(new BigDecimal("100000"))
                .position(Position.WORKER)
                .workExperienceTotal(60)
                .workExperienceCurrent(24)
                .build();
    }

    public static ScoringDataDto createValidScoringRequest() {
        return ScoringDataDto.builder()
                .amount(new BigDecimal("1000000"))
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthday(LocalDate.now().minusYears(30))
                .gender(Gender.MALE)
                .passportSeries("1234")
                .passportNumber("123456")
                .passportIssueDate(LocalDate.now().minusYears(5))
                .passportIssueBranch("Branch")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(1)
                .employment(createValidEmployment())
                .accountNumber("1234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
    }

    public static EmploymentDto createEmploymentWithParams(
            EmploymentStatus status,
            BigDecimal salary,
            Position position,
            int totalExp,
            int currentExp) {
        return EmploymentDto.builder()
                .employmentStatus(status)
                .salary(salary)
                .position(position)
                .workExperienceTotal(totalExp)
                .workExperienceCurrent(currentExp)
                .build();
    }

    public static ScoringDataDto createScoringRequestWithParams(
            BigDecimal amount,
            int term,
            int age,
            Gender gender,
            MaritalStatus maritalStatus,
            int dependentAmount,
            EmploymentDto employment,
            boolean isInsuranceEnabled,
            boolean isSalaryClient) {
        return ScoringDataDto.builder()
                .amount(amount)
                .term(term)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthday(LocalDate.now().minusYears(age))
                .gender(gender)
                .passportSeries("1234")
                .passportNumber("123456")
                .passportIssueDate(LocalDate.now().minusYears(5))
                .passportIssueBranch("Branch")
                .maritalStatus(maritalStatus)
                .dependentAmount(dependentAmount)
                .employment(employment)
                .accountNumber("1234567890")
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

    public static LoanOfferDto createLoanOfferWithTotal(
            BigDecimal requestedAmount,
            BigDecimal totalAmount,
            Integer term,
            BigDecimal monthlyPayment,
            BigDecimal rate,
            boolean isInsuranceEnabled,
            boolean isSalaryClient) {
        return LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(requestedAmount)
                .totalAmount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

    public static LoanOfferDto createLoanOffer(
            BigDecimal amount,
            int term,
            BigDecimal monthlyPayment,
            BigDecimal rate,
            boolean isInsuranceEnabled,
            boolean isSalaryClient) {
        return LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(amount)
                .totalAmount(amount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

    public static CreditDto createExpectedCreditDto(
            BigDecimal amount,
            int term,
            BigDecimal monthlyPayment,
            BigDecimal rate,
            boolean isInsuranceEnabled,
            boolean isSalaryClient) {
        return CreditDto.builder()
                .amount(amount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .psk(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .paymentSchedule(new ArrayList<>())
                .build();
    }

    public static LoanStatementRequestDto createValidLoanStatementRequest() {
        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthday(LocalDate.now().minusYears(30))
                .email("ivan@example.com")
                .passportSeries("1234")
                .passportNumber("123456")
                .build();
    }

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


    public static ScoringDataDto createScoringRequestWithAge(int age) {
        return ScoringDataDto.builder()
                .amount(new BigDecimal("1000000"))
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthday(LocalDate.now().minusYears(age))
                .gender(Gender.MALE)
                .passportSeries("1234")
                .passportNumber("123456")
                .passportIssueDate(LocalDate.now().minusYears(5))
                .passportIssueBranch("Branch")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(1)
                .employment(createValidEmployment())
                .accountNumber("1234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
    }


    public static LoanStatementRequestDto createLoanStatementRequestWithAmount(BigDecimal amount) {
        return LoanStatementRequestDto.builder()
                .amount(amount)
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .birthday(LocalDate.now().minusYears(30))
                .email("ivan@example.com")
                .passportSeries("1234")
                .passportNumber("123456")
                .build();
    }

}