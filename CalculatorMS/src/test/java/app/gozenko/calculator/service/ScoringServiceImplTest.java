package app.gozenko.calculator.service;

import app.gozenko.calculator.utils.TestDataGenerator;
import app.gozenko.dto.CreditDto;
import app.gozenko.dto.EmploymentDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.enums.*;
import app.gozenko.exception.UnScoringDataException;
import app.gozenko.service.CalcCreditValueService;
import app.gozenko.service.CheckValueService;
import app.gozenko.service.ScoringServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringServiceImplTest {

    @Mock
    private CheckValueService checkValueService;

    @Mock
    private CalcCreditValueService calcCreditValueService;

    @InjectMocks
    private ScoringServiceImpl scoringService;

    private ScoringDataDto baseScoringData;
    private BigDecimal baseRate;
    private BigDecimal expectedMonthlyPayment;
    private LoanOfferDto baseLoanOffer;
    private CreditDto expectedCreditDto;

    @BeforeEach
    void setUp() {
        baseRate = new BigDecimal("20.00");
        expectedMonthlyPayment = new BigDecimal("92635.22");

        ReflectionTestUtils.setField(scoringService, "rateToManager", new BigDecimal("2"));
        ReflectionTestUtils.setField(scoringService, "rateToTopManager", new BigDecimal("3"));
        ReflectionTestUtils.setField(scoringService, "rateMarried", new BigDecimal("2"));
        ReflectionTestUtils.setField(scoringService, "rateDivorced", new BigDecimal("1"));
        ReflectionTestUtils.setField(scoringService, "rateWithAge", new BigDecimal("3"));
        ReflectionTestUtils.setField(scoringService, "rateWithDependent", new BigDecimal("1"));

        baseScoringData = TestDataGenerator.createValidScoringRequest();

        baseLoanOffer = TestDataGenerator.createLoanOffer(
                new BigDecimal("1000000"),
                12,
                expectedMonthlyPayment,
                baseRate,
                true,
                true
        );

        expectedCreditDto = TestDataGenerator.createExpectedCreditDto(
                new BigDecimal("1000000"),
                12,
                expectedMonthlyPayment,
                baseRate,
                true,
                true
        );
    }

    @Test
    @DisplayName("Успешный расчет кредита для хорошего клиента")
    void createScoringData_Success() {
        when(checkValueService.isSalaryAndInsurance(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(baseLoanOffer);

        when(calcCreditValueService.mainCounting(any(ScoringDataDto.class), any(BigDecimal.class)))
                .thenReturn(expectedCreditDto);

        CreditDto result = scoringService.createScoringData(baseScoringData);

        assertAll("Проверка всех полей кредита",
                () -> assertNotNull(result),
                () -> assertEquals(expectedCreditDto.getAmount(), result.getAmount()),
                () -> assertEquals(expectedCreditDto.getTerm(), result.getTerm()),
                () -> assertEquals(expectedCreditDto.getMonthlyPayment(), result.getMonthlyPayment()),
                () -> assertEquals(expectedCreditDto.getRate(), result.getRate()),
                () -> assertEquals(expectedCreditDto.getIsInsuranceEnabled(), result.getIsInsuranceEnabled()),
                () -> assertEquals(expectedCreditDto.getIsSalaryClient(), result.getIsSalaryClient())
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidationScenarios")
    @DisplayName("Проверка валидационных исключений")
    void createScoringData_ValidationFailed_ThrowsException(
            EmploymentStatus employmentStatus,
            BigDecimal amount,
            BigDecimal salary,
            int age,
            int totalExp,
            int currentExp,
            String expectedMessage) {

        EmploymentDto employment = TestDataGenerator.createEmploymentWithParams(
                employmentStatus,
                salary,
                Position.WORKER,
                totalExp,
                currentExp
        );

        ScoringDataDto request = TestDataGenerator.createScoringRequestWithParams(
                amount,
                12,
                age,
                Gender.MALE,
                MaritalStatus.MARRIED,
                1,
                employment,
                true,
                true
        );

        UnScoringDataException exception = assertThrows(
                UnScoringDataException.class,
                () -> scoringService.createScoringData(request)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> provideValidationScenarios() {
        return Stream.of(
                Arguments.of(
                        EmploymentStatus.NOT_WORK,
                        new BigDecimal("1000000"),
                        new BigDecimal("100000"),
                        30, 60, 24,
                        "Безработный"
                ),
                Arguments.of(
                        EmploymentStatus.WORK,
                        new BigDecimal("2500000"),
                        new BigDecimal("100000"),
                        30, 60, 24,
                        "Сумма займа больше, чем 24 зарплаты"
                ),
                Arguments.of(
                        EmploymentStatus.WORK,
                        new BigDecimal("1000000"),
                        new BigDecimal("100000"),
                        19, 60, 24,
                        "Моложе 20"
                ),
                Arguments.of(
                        EmploymentStatus.WORK,
                        new BigDecimal("1000000"),
                        new BigDecimal("100000"),
                        66, 60, 24,
                        "Старше 65"
                ),
                Arguments.of(
                        EmploymentStatus.WORK,
                        new BigDecimal("1000000"),
                        new BigDecimal("100000"),
                        30, 6, 24,
                        "Общий стаж работы менее 12 месяцев"
                ),
                Arguments.of(
                        EmploymentStatus.WORK,
                        new BigDecimal("1000000"),
                        new BigDecimal("100000"),
                        30, 60, 2,
                        "Текущий стаж работы менее 3 месяцев"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideRateCalculationScenarios")
    @DisplayName("Проверка расчета финальной ставки")
    void createScoringData_RateCalculations_CorrectRate(
            Position position,
            MaritalStatus maritalStatus,
            Gender gender,
            int age,
            int dependentAmount,
            boolean isInsuranceEnabled,
            boolean isSalaryClient,
            BigDecimal expectedRate) {

        EmploymentDto employment = TestDataGenerator.createEmploymentWithParams(
                EmploymentStatus.WORK,
                new BigDecimal("100000"),
                position,
                60,
                24
        );

        ScoringDataDto request = TestDataGenerator.createScoringRequestWithParams(
                new BigDecimal("1000000"),
                12,
                age,
                gender,
                maritalStatus,
                dependentAmount,
                employment,
                isInsuranceEnabled,
                isSalaryClient
        );

        LoanOfferDto loanOffer = TestDataGenerator.createLoanOffer(
                new BigDecimal("1000000"),
                12,
                expectedMonthlyPayment,
                new BigDecimal("20.00"),
                isInsuranceEnabled,
                isSalaryClient
        );

        CreditDto expectedCredit = TestDataGenerator.createExpectedCreditDto(
                new BigDecimal("1000000"),
                12,
                expectedMonthlyPayment,
                expectedRate,
                isInsuranceEnabled,
                isSalaryClient
        );

        if (isInsuranceEnabled && isSalaryClient) {
            when(checkValueService.isSalaryAndInsurance(any(BigDecimal.class), any(Integer.class)))
                    .thenReturn(loanOffer);
        } else if (isInsuranceEnabled) {
            when(checkValueService.isInsurance(any(BigDecimal.class), any(Integer.class)))
                    .thenReturn(loanOffer);
        } else if (isSalaryClient) {
            when(checkValueService.isSalary(any(BigDecimal.class), any(Integer.class)))
                    .thenReturn(loanOffer);
        } else {
            when(checkValueService.noneSalaryAndInsurance(any(BigDecimal.class), any(Integer.class)))
                    .thenReturn(loanOffer);
        }

        when(calcCreditValueService.mainCounting(any(ScoringDataDto.class), any(BigDecimal.class)))
                .thenReturn(expectedCredit);

        CreditDto result = scoringService.createScoringData(request);

        assertNotNull(result);
        assertEquals(expectedRate, result.getRate(),
                String.format("Для параметров: position=%s, marital=%s, gender=%s, age=%d, dependents=%d, isInsurance=%s, isSalary=%s",
                        position, maritalStatus, gender, age, dependentAmount, isInsuranceEnabled, isSalaryClient));
    }

    private static Stream<Arguments> provideRateCalculationScenarios() {
        return Stream.of(
                Arguments.of(
                        Position.WORKER, MaritalStatus.MARRIED,
                        Gender.MALE, 30, 1, true, true,
                        new BigDecimal("18.00")
                ),
                Arguments.of(
                        Position.MANAGER, MaritalStatus.MARRIED,
                        Gender.MALE, 30, 1, true, true,
                        new BigDecimal("16.00")
                ),
                Arguments.of(
                        Position.TOP_MANAGER, MaritalStatus.MARRIED,
                        Gender.MALE, 30, 1, true, true,
                        new BigDecimal("15.00")
                ),
                Arguments.of(
                        Position.WORKER, MaritalStatus.DIVORCED,
                        Gender.MALE, 30, 1, true, true,
                        new BigDecimal("21.00")
                ),
                Arguments.of(
                        Position.WORKER, MaritalStatus.MARRIED,
                        Gender.MALE, 35, 1, true, true,
                        new BigDecimal("15.00")
                ),
                Arguments.of(
                        Position.WORKER, MaritalStatus.MARRIED,
                        Gender.FEMALE, 40, 1, true, true,
                        new BigDecimal("15.00")
                ),
                Arguments.of(
                        Position.WORKER, MaritalStatus.MARRIED,
                        Gender.MALE, 30, 3, true, true,
                        new BigDecimal("17.00")
                ),
                Arguments.of(
                        Position.WORKER, MaritalStatus.MARRIED,
                        Gender.MALE, 30, 1, true, false,
                        new BigDecimal("18.00")
                ),
                Arguments.of(
                        Position.WORKER, MaritalStatus.MARRIED,
                        Gender.MALE, 30, 1, false, true,
                        new BigDecimal("18.00")
                ),
                Arguments.of(
                        Position.WORKER, MaritalStatus.MARRIED,
                        Gender.MALE, 30, 1, false, false,
                        new BigDecimal("18.00")
                )
        );
    }
}