package app.gozenko.calculator.service;

import app.gozenko.calculator.utils.StubGenerator;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.service.CalcCreditValueService;
import app.gozenko.service.CheckValueService;
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
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckValueServiceTest {

    @Mock
    private CalcCreditValueService calcCreditValueService;

    @InjectMocks
    private CheckValueService checkValueService;

    private BigDecimal baseRate;
    private BigDecimal insuranceRate;
    private BigDecimal salaryRate;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal expectedMonthlyPayment;

    @BeforeEach
    void setUp() {
        baseRate = new BigDecimal("20.00");
        insuranceRate = new BigDecimal("2.00");
        salaryRate = new BigDecimal("1.00");
        amount = new BigDecimal("300000");
        term = 12;
        expectedMonthlyPayment = new BigDecimal("27790.57");

        ReflectionTestUtils.setField(checkValueService, "propertyRate", baseRate);
        ReflectionTestUtils.setField(checkValueService, "insuranceRate", insuranceRate);
        ReflectionTestUtils.setField(checkValueService, "rateSalaryClient", salaryRate);
    }

    @Test
    @DisplayName("Проверка salaryAndInsuranceClient - со страховкой и зарплатным клиентом")
    void salaryAndInsuranceClient_Success() {
        BigDecimal expectedRate = baseRate.subtract(insuranceRate).subtract(salaryRate);
        BigDecimal insurancePrice = amount.multiply(insuranceRate.divide(BigDecimal.valueOf(100)));
        BigDecimal expectedTotalAmount = amount.add(insurancePrice);

        when(calcCreditValueService.calcMonthlyPayment(expectedRate, expectedTotalAmount, term))
                .thenReturn(expectedMonthlyPayment);

        LoanOfferDto result = checkValueService.salaryAndInsuranceClient(amount, term);

        LoanOfferDto expected = StubGenerator.createLoanOfferWithTotal(
                amount,
                expectedTotalAmount,
                term,
                expectedMonthlyPayment,
                expectedRate,
                true,
                true
        );

        assertAll("Проверка salaryAndInsuranceClient",
                () -> assertNotNull(result),
                () -> assertNotNull(result.getStatementId()),
                () -> assertEquals(expected.getRequestedAmount(), result.getRequestedAmount()),
                () -> assertEquals(expected.getTotalAmount(), result.getTotalAmount()),
                () -> assertEquals(expected.getTerm(), result.getTerm()),
                () -> assertEquals(expected.getMonthlyPayment(), result.getMonthlyPayment()),
                () -> assertEquals(expected.getRate(), result.getRate()),
                () -> assertEquals(expected.getIsInsuranceEnabled(), result.getIsInsuranceEnabled()),
                () -> assertEquals(expected.getIsSalaryClient(), result.getIsSalaryClient())
        );
    }

    @Test
    @DisplayName("Проверка salaryClient - только зарплатный клиент")
    void salaryClient_Success() {
        BigDecimal expectedRate = baseRate.subtract(salaryRate);

        when(calcCreditValueService.calcMonthlyPayment(expectedRate, amount, term))
                .thenReturn(expectedMonthlyPayment);

        LoanOfferDto result = checkValueService.salaryClient(amount, term);

        LoanOfferDto expected = StubGenerator.createLoanOffer(
                amount,
                term,
                expectedMonthlyPayment,
                expectedRate,
                false,
                true
        );

        assertAll("Проверка salaryClient",
                () -> assertNotNull(result),
                () -> assertNotNull(result.getStatementId()),
                () -> assertEquals(expected.getRequestedAmount(), result.getRequestedAmount()),
                () -> assertEquals(expected.getTotalAmount(), result.getTotalAmount()),
                () -> assertEquals(expected.getTerm(), result.getTerm()),
                () -> assertEquals(expected.getMonthlyPayment(), result.getMonthlyPayment()),
                () -> assertEquals(expected.getRate(), result.getRate()),
                () -> assertEquals(expected.getIsInsuranceEnabled(), result.getIsInsuranceEnabled()),
                () -> assertEquals(expected.getIsSalaryClient(), result.getIsSalaryClient())
        );
    }

    @Test
    @DisplayName("Проверка insuranceClient - только со страховкой")
    void insuranceClient_Success() {
        BigDecimal expectedRate = baseRate.subtract(insuranceRate);
        BigDecimal insurancePrice = amount.multiply(
                insuranceRate.divide(BigDecimal.valueOf(100), RoundingMode.FLOOR));
        BigDecimal expectedTotalAmount = amount.add(insurancePrice);

        when(calcCreditValueService.calcMonthlyPayment(expectedRate, expectedTotalAmount, term))
                .thenReturn(expectedMonthlyPayment);

        LoanOfferDto result = checkValueService.insuranceClient(amount, term);

        LoanOfferDto expected = StubGenerator.createLoanOfferWithTotal(
                amount,
                expectedTotalAmount,
                term,
                expectedMonthlyPayment,
                expectedRate,
                true,
                false
        );

        assertAll("Проверка insuranceClient",
                () -> assertNotNull(result),
                () -> assertNotNull(result.getStatementId()),
                () -> assertEquals(expected.getRequestedAmount(), result.getRequestedAmount()),
                () -> assertEquals(expected.getTotalAmount(), result.getTotalAmount()),
                () -> assertEquals(expected.getTerm(), result.getTerm()),
                () -> assertEquals(expected.getMonthlyPayment(), result.getMonthlyPayment()),
                () -> assertEquals(expected.getRate(), result.getRate()),
                () -> assertEquals(expected.getIsInsuranceEnabled(), result.getIsInsuranceEnabled()),
                () -> assertEquals(expected.getIsSalaryClient(), result.getIsSalaryClient())
        );
    }

    @Test
    @DisplayName("Проверка noneSalaryAndInsuranceClient - без страховки и без зарплаты")
    void noneSalaryAndInsuranceClient_Success() {
        when(calcCreditValueService.calcMonthlyPayment(baseRate, amount, term))
                .thenReturn(expectedMonthlyPayment);

        LoanOfferDto result = checkValueService.noneSalaryAndInsuranceClient(amount, term);

        LoanOfferDto expected = StubGenerator.createLoanOffer(
                amount,
                term,
                expectedMonthlyPayment,
                baseRate,
                false,
                false
        );

        assertAll("Проверка noneSalaryAndInsuranceClient",
                () -> assertNotNull(result),
                () -> assertNotNull(result.getStatementId()),
                () -> assertEquals(expected.getRequestedAmount(), result.getRequestedAmount()),
                () -> assertEquals(expected.getTotalAmount(), result.getTotalAmount()),
                () -> assertEquals(expected.getTerm(), result.getTerm()),
                () -> assertEquals(expected.getMonthlyPayment(), result.getMonthlyPayment()),
                () -> assertEquals(expected.getRate(), result.getRate()),
                () -> assertEquals(expected.getIsInsuranceEnabled(), result.getIsInsuranceEnabled()),
                () -> assertEquals(expected.getIsSalaryClient(), result.getIsSalaryClient())
        );
    }

    @ParameterizedTest
    @MethodSource("provideAmountAndTermScenarios")
    @DisplayName("Проверка salaryAndInsuranceClient с разными суммами и сроками")
    void salaryAndInsuranceClient_DifferentAmountsAndTerms(
            BigDecimal amount,
            Integer term,
            BigDecimal expectedTotalAmount) {

        BigDecimal expectedRate = baseRate.subtract(insuranceRate).subtract(salaryRate);
        BigDecimal monthlyPayment = new BigDecimal("50000.00");

        when(calcCreditValueService.calcMonthlyPayment(expectedRate, expectedTotalAmount, term))
                .thenReturn(monthlyPayment);

        LoanOfferDto result = checkValueService.salaryAndInsuranceClient(amount, term);

        LoanOfferDto expected = StubGenerator.createLoanOfferWithTotal(
                amount,
                expectedTotalAmount,
                term,
                monthlyPayment,
                expectedRate,
                true,
                true
        );

        assertEquals(expected.getTotalAmount(), result.getTotalAmount());
        assertEquals(expected.getTotalAmount().subtract(expected.getRequestedAmount()),
                result.getTotalAmount().subtract(result.getRequestedAmount()));
    }

    private static Stream<Arguments> provideAmountAndTermScenarios() {
        return Stream.of(
                Arguments.of(
                        new BigDecimal("100000"), 6,
                        new BigDecimal("102000.00")
                ),
                Arguments.of(
                        new BigDecimal("500000"), 12,
                        new BigDecimal("510000.00")
                ),
                Arguments.of(
                        new BigDecimal("1000000"), 24,
                        new BigDecimal("1020000.00")
                ),
                Arguments.of(
                        new BigDecimal("2000000"), 36,
                        new BigDecimal("2040000.00")
                )
        );
    }

    @Test
    @DisplayName("Проверка расчета страховки в insuranceClient с RoundingMode.FLOOR")
    void insuranceClient_InsuranceCalculationWithFloor() {
        amount = new BigDecimal("100000.55");
        BigDecimal expectedInsurancePrice = amount.multiply(
                insuranceRate.divide(BigDecimal.valueOf(100), RoundingMode.FLOOR));
        BigDecimal expectedTotalAmount = amount.add(expectedInsurancePrice);
        BigDecimal expectedRate = baseRate.subtract(insuranceRate);

        when(calcCreditValueService.calcMonthlyPayment(expectedRate, expectedTotalAmount, term))
                .thenReturn(expectedMonthlyPayment);

        LoanOfferDto result = checkValueService.insuranceClient(amount, term);

        LoanOfferDto expected = StubGenerator.createLoanOfferWithTotal(
                amount,
                expectedTotalAmount,
                term,
                expectedMonthlyPayment,
                expectedRate,
                true,
                false
        );

        assertEquals(expected.getTotalAmount(), result.getTotalAmount());
    }

    @Test
    @DisplayName("Проверка с нулевой суммой")
    void checkValueService_WithZeroAmount() {
        amount = BigDecimal.ZERO;

        when(calcCreditValueService.calcMonthlyPayment(baseRate, amount, term))
                .thenReturn(BigDecimal.ZERO);

        LoanOfferDto result = checkValueService.noneSalaryAndInsuranceClient(amount, term);

        LoanOfferDto expected = StubGenerator.createLoanOffer(
                amount,
                term,
                BigDecimal.ZERO,
                baseRate,
                false,
                false
        );

        assertAll("Проверка с нулевой суммой",
                () -> assertEquals(expected.getRequestedAmount(), result.getRequestedAmount()),
                () -> assertEquals(expected.getTotalAmount(), result.getTotalAmount()),
                () -> assertEquals(expected.getMonthlyPayment(), result.getMonthlyPayment())
        );
    }
}