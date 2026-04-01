package app.gozenko.DealMS.utils;

import app.gozenko.dto.*;
import app.gozenko.entity.Statement;
import app.gozenko.entity.Client;
import app.gozenko.entity.Credit;
import app.gozenko.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StubGenerator {

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

    public static LoanStatementRequestDto createLoanStatementRequestWithParams(
            BigDecimal amount,
            int term,
            String firstName,
            String lastName,
            String middleName,
            LocalDate birthday,
            String email,
            String passportSeries,
            String passportNumber) {
        return LoanStatementRequestDto.builder()
                .amount(amount)
                .term(term)
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .birthday(birthday)
                .email(email)
                .passportSeries(passportSeries)
                .passportNumber(passportNumber)
                .build();
    }

    public static FinishRegistrationRequestDto createValidFinishRegistrationRequest() {
        return FinishRegistrationRequestDto.builder()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(1)
                .passportIssueDate(LocalDate.now().minusYears(5))
                .passportIssueBranch("Branch")
                .employment(createValidEmploymentDto())
                .accountNumber("1234567890")
                .build();
    }

    public static FinishRegistrationRequestDto createFinishRegistrationRequestWithParams(
            Gender gender,
            MaritalStatus maritalStatus,
            int dependentAmount,
            LocalDate passportIssueDate,
            String passportIssueBranch,
            EmploymentDto employment,
            String accountNumber) {
        return FinishRegistrationRequestDto.builder()
                .gender(gender)
                .maritalStatus(maritalStatus)
                .dependentAmount(dependentAmount)
                .passportIssueDate(passportIssueDate)
                .passportIssueBranch(passportIssueBranch)
                .employment(employment)
                .accountNumber(accountNumber)
                .build();
    }

    public static EmploymentDto createValidEmploymentDto() {
        return EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerINN("123456789012")
                .salary(new BigDecimal("100000"))
                .position(Position.WORKER)
                .workExperienceTotal(60)
                .workExperienceCurrent(24)
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
                .employerINN("123456789012")
                .salary(salary)
                .position(position)
                .workExperienceTotal(totalExp)
                .workExperienceCurrent(currentExp)
                .build();
    }

    public static List<LoanOfferDto> createLoanOffersList() {
        return List.of(
                createLoanOffer(new BigDecimal("1000000"), 12, new BigDecimal("92635.22"),
                        new BigDecimal("20.00"), false, false),
                createLoanOffer(new BigDecimal("1000000"), 12, new BigDecimal("87550.15"),
                        new BigDecimal("18.00"), true, false),
                createLoanOffer(new BigDecimal("1000000"), 12, new BigDecimal("85000.00"),
                        new BigDecimal("17.00"), false, true),
                createLoanOffer(new BigDecimal("1000000"), 12, new BigDecimal("80000.50"),
                        new BigDecimal("15.00"), true, true)
        );
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

    public static LoanOfferDto createLoanOfferWithTotal(
            BigDecimal requestedAmount,
            BigDecimal totalAmount,
            int term,
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

    public static ScoringDataDto createValidScoringData() {
        return ScoringDataDto.builder()
                .amount(new BigDecimal("1000000"))
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .gender(Gender.MALE)
                .birthday(LocalDate.now().minusYears(30))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.now().minusYears(5))
                .passportIssueBranch("Branch")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(1)
                .employment(createValidEmploymentDto())
                .accountNumber("1234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
    }

    public static CreditDto createCreditDto() {
        return CreditDto.builder()
                .amount(new BigDecimal("1000000"))
                .term(12)
                .monthlyPayment(new BigDecimal("92635.22"))
                .rate(new BigDecimal("20.00"))
                .psk(new BigDecimal("1200000"))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .paymentSchedule(new ArrayList<>())
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

    public static Client createClient() {
        return createClient(UUID.randomUUID());
    }

    public static Client createClient(UUID id) {
        PassportDto passport = PassportDto.builder()
                .series("1234")
                .number("567890")
                .issueDate(LocalDate.now().minusYears(5))
                .issueBranch("Branch")
                .build();

        return Client.builder()
                .id(id)
                .lastName("Ivanov")
                .firstName("Ivan")
                .middleName("Ivanovich")
                .birthday(LocalDate.now().minusYears(30))
                .email("ivan@example.com")
                .passport(passport)
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(1)
                .employment(createValidEmploymentDto())
                .accountNumber("1234567890")
                .build();
    }

    public static Client createClientWithParams(
            UUID id,
            String lastName,
            String firstName,
            String middleName,
            LocalDate birthday,
            String email,
            PassportDto passport,
            Gender gender,
            MaritalStatus maritalStatus,
            int dependentAmount,
            EmploymentDto employment,
            String accountNumber) {
        return Client.builder()
                .id(id)
                .lastName(lastName)
                .firstName(firstName)
                .middleName(middleName)
                .birthday(birthday)
                .email(email)
                .passport(passport)
                .gender(gender)
                .maritalStatus(maritalStatus)
                .dependentAmount(dependentAmount)
                .employment(employment)
                .accountNumber(accountNumber)
                .build();
    }

    public static Credit createCredit() {
        return createCredit(UUID.randomUUID());
    }

    public static Credit createCredit(UUID id) {
        return Credit.builder()
                .id(id)
                .amount(new BigDecimal("1000000"))
                .term(12)
                .monthlyPayment(new BigDecimal("92635.22"))
                .rate(new BigDecimal("20.00"))
                .psk(new BigDecimal("1200000"))
                .paymentSchedule(new ArrayList<>())
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .creditStatus(CreditStatus.CALCULATED)
                .build();
    }

    public static Statement createStatement() {
        return createStatement(UUID.randomUUID(), createClient());
    }

    public static Statement createStatement(UUID statementId, Client client) {
        List<StatementStatusHistoryDto> statusHistory = new ArrayList<>();
        statusHistory.add(StatementStatusHistoryDto.builder()
                .status(StatementStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(StatusChangeType.AUTOMATIC)
                .build());

        return Statement.builder()
                .id(statementId)
                .client(client)
                .status(StatementStatus.PREAPPROVAL)
                .creationDate(LocalDateTime.now())
                .appliedOffer(createLoanOffer(new BigDecimal("1000000"), 12,
                        new BigDecimal("92635.22"), new BigDecimal("20.00"), false, false))
                .credit(createCredit())
                .signDate(null)
                .statusHistory(statusHistory)
                .sesCode(45)
                .build();
    }

    public static Statement createStatementWithClient(Client client) {
        List<StatementStatusHistoryDto> statusHistory = new ArrayList<>();
        statusHistory.add(StatementStatusHistoryDto.builder()
                .status(StatementStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(StatusChangeType.AUTOMATIC)
                .build());

        return Statement.builder()
                .id(UUID.randomUUID())
                .client(client)
                .status(StatementStatus.PREAPPROVAL)
                .creationDate(LocalDateTime.now())
                .sesCode(45)
                .statusHistory(statusHistory)
                .build();
    }

    public static PassportDto createValidPassportDto() {
        return PassportDto.builder()
                .series("1234")
                .number("567890")
                .issueDate(LocalDate.now().minusYears(5))
                .issueBranch("Branch")
                .build();
    }

}