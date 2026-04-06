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
            String firstName,
            String lastName,
            String middleName,
            LocalDate birthday,
            String email,
            String passportSeries,
            String passportNumber) {
        return LoanStatementRequestDto.builder()
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
                .accountNumber("1234567890")
                .build();
    }

    public static FinishRegistrationRequestDto createFinishRegistrationRequestWithParams(
            Gender gender,
            MaritalStatus maritalStatus,
            EmploymentDto employmentDto,
            int dependentAmount
    ) {
        return FinishRegistrationRequestDto.builder()
                .gender(gender)
                .maritalStatus(maritalStatus)
                .dependentAmount(dependentAmount)
                .passportIssueDate(LocalDate.of(2020, 1, 1))
                .passportIssueBranch("Branch")
                .accountNumber("1234567890")
                .employment(employmentDto)
                .build();
    }

    public static EmploymentDto createValidEmploymentDtoWithPosition(Position position) {
        return EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerINN("123456789012")
                .salary(new BigDecimal("100000"))
                .position(position)
                .workExperienceTotal(60)
                .workExperienceCurrent(24)
                .build();
    }

    public static List<LoanOfferDto> createExpectedLoanOffersList() {
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

    public static ScoringDataDto createExpectedScoringData() {
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
                .accountNumber("1234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
    }

    public static CreditDto createExpectedCreditDto() {
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

    public static Client createClient() {
        PassportDto passport = PassportDto.builder()
                .series("1234")
                .number("567890")
                .issueDate(LocalDate.now().minusYears(5))
                .issueBranch("Branch")
                .build();

        return Client.builder()
                .id(UUID.randomUUID())
                .lastName("Ivanov")
                .firstName("Ivan")
                .middleName("Ivanovich")
                .birthday(LocalDate.now().minusYears(30))
                .email("ivan@example.com")
                .passport(passport)
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(1)
                .accountNumber("1234567890")
                .build();
    }

    public static Client createPartOfClientWithParams(
            UUID id,
            String lastName,
            String firstName,
            String middleName,
            LocalDate birthday,
            String email,
            PassportDto passport
    ) {
        return Client.builder()
                .id(id)
                .lastName(lastName)
                .firstName(firstName)
                .middleName(middleName)
                .birthday(birthday)
                .email(email)
                .passport(passport)
                .build();
    }

    public static Credit createCredit() {
        return Credit.builder()
                .id(UUID.randomUUID())
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
        List<StatementStatusHistoryDto> statusHistory = new ArrayList<>();
        statusHistory.add(StatementStatusHistoryDto.builder()
                .status(StatementStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(StatusChangeType.AUTOMATIC)
                .build());

        return Statement.builder()
                .id(UUID.randomUUID())
                .client(createClient())
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

    public static PassportDto createPassportDtoWithSeriesAndNumber() {
        return PassportDto.builder()
                .series("1234")
                .number("567890")
                .build();
    }

}