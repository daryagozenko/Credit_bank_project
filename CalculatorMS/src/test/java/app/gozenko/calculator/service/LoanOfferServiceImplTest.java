package app.gozenko.calculator.service;

import app.gozenko.calculator.utils.StubGenerator;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.service.CheckValueService;
import app.gozenko.service.LoanOfferServiceImpl;
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
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanOfferServiceImplTest {

    @Mock
    private CheckValueService checkValueService;

    @InjectMocks
    private LoanOfferServiceImpl loanOfferService;

    private BigDecimal baseAmount;
    private Integer baseTerm;
    private BigDecimal baseRate;
    private BigDecimal insuranceRate;
    private BigDecimal expectedMonthlyPayment;
    private LoanOfferDto salaryAndInsuranceOffer;
    private LoanOfferDto insuranceOffer;
    private LoanOfferDto salaryOffer;
    private LoanOfferDto noneOffer;

    @BeforeEach
    void setUp() {
        BigDecimal salaryRate = new BigDecimal("1.00");
        baseAmount = new BigDecimal("300000");
        baseTerm = 12;
        baseRate = new BigDecimal("20.00");
        insuranceRate = new BigDecimal("2.00");
        expectedMonthlyPayment = new BigDecimal("27790.57");

        ReflectionTestUtils.setField(checkValueService, "baseRate", baseRate);
        ReflectionTestUtils.setField(checkValueService, "insuranceRate", insuranceRate);
        ReflectionTestUtils.setField(checkValueService, "rateSalaryClient", salaryRate);

        salaryAndInsuranceOffer = StubGenerator.createLoanOffer(
                baseAmount,
                baseTerm,
                expectedMonthlyPayment,
                baseRate.subtract(insuranceRate).subtract(salaryRate), // 17.00
                true,
                true
        );

        insuranceOffer = StubGenerator.createLoanOffer(
                baseAmount,
                baseTerm,
                expectedMonthlyPayment,
                baseRate.subtract(insuranceRate), // 18.00
                true,
                false
        );

        salaryOffer = StubGenerator.createLoanOffer(
                baseAmount,
                baseTerm,
                expectedMonthlyPayment,
                baseRate.subtract(salaryRate), // 19.00
                false,
                true
        );

        noneOffer = StubGenerator.createLoanOffer(
                baseAmount,
                baseTerm,
                expectedMonthlyPayment,
                baseRate, // 20.00
                false,
                false
        );
    }

    @Test
    @DisplayName("Успешное создание списка кредитных предложений")
    void createLoanOffers_Success() {
        when(checkValueService.createSalaryAndInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryAndInsuranceOffer);
        when(checkValueService.createInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(insuranceOffer);
        when(checkValueService.createSalaryLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryOffer);
        when(checkValueService.createDefaultLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(noneOffer);

        List<LoanOfferDto> result = loanOfferService.createLoanOffers(baseAmount, baseTerm);

        assertAll("Проверка списка кредитных предложений",
                () -> assertNotNull(result),
                () -> assertEquals(4, result.size(), "Должно быть 4 предложения"),

                // Проверка первого предложения (со страховкой и зарплатой)
                () -> {
                    LoanOfferDto offer = result.get(0);
                    assertEquals(baseAmount, offer.getRequestedAmount());
                    assertEquals(baseTerm, offer.getTerm());
                    assertTrue(offer.getIsInsuranceEnabled());
                    assertTrue(offer.getIsSalaryClient());
                    assertEquals(new BigDecimal("17.00"), offer.getRate());
                },

                // Проверка второго предложения (только со страховкой)
                () -> {
                    LoanOfferDto offer = result.get(1);
                    assertEquals(baseAmount, offer.getRequestedAmount());
                    assertEquals(baseTerm, offer.getTerm());
                    assertTrue(offer.getIsInsuranceEnabled());
                    assertFalse(offer.getIsSalaryClient());
                    assertEquals(new BigDecimal("18.00"), offer.getRate());
                },

                // Проверка третьего предложения (только зарплатный)
                () -> {
                    LoanOfferDto offer = result.get(2);
                    assertEquals(baseAmount, offer.getRequestedAmount());
                    assertEquals(baseTerm, offer.getTerm());
                    assertFalse(offer.getIsInsuranceEnabled());
                    assertTrue(offer.getIsSalaryClient());
                    assertEquals(new BigDecimal("19.00"), offer.getRate());
                },

                // Проверка четвертого предложения (без страховки и зарплаты)
                () -> {
                    LoanOfferDto offer = result.get(3);
                    assertEquals(baseAmount, offer.getRequestedAmount());
                    assertEquals(baseTerm, offer.getTerm());
                    assertFalse(offer.getIsInsuranceEnabled());
                    assertFalse(offer.getIsSalaryClient());
                    assertEquals(new BigDecimal("20.00"), offer.getRate());
                }
        );
    }

    @Test
    @DisplayName("Проверка порядка предложений в списке")
    void createLoanOffers_OrderCheck() {
        when(checkValueService.createSalaryAndInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryAndInsuranceOffer);
        when(checkValueService.createInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(insuranceOffer);
        when(checkValueService.createSalaryLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryOffer);
        when(checkValueService.createDefaultLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(noneOffer);

        List<LoanOfferDto> result = loanOfferService.createLoanOffers(baseAmount, baseTerm);

        assertTrue(result.get(0).getIsInsuranceEnabled() && result.get(0).getIsSalaryClient());
        assertTrue(result.get(1).getIsInsuranceEnabled() && !result.get(1).getIsSalaryClient());
        assertTrue(!result.get(2).getIsInsuranceEnabled() && result.get(2).getIsSalaryClient());
        assertTrue(!result.get(3).getIsInsuranceEnabled() && !result.get(3).getIsSalaryClient());
    }

    @ParameterizedTest
    @MethodSource("provideAmountAndTermScenarios")
    @DisplayName("Проверка создания предложений для разных сумм и сроков")
    void createLoanOffers_DifferentAmountsAndTerms(
            BigDecimal amount,
            Integer term,
            BigDecimal expectedRateWithInsuranceAndSalary,
            BigDecimal expectedRateWithInsurance,
            BigDecimal expectedRateWithSalary,
            BigDecimal expectedRateNone) {

        LoanOfferDto salaryAndInsuranceOffer = StubGenerator.createLoanOffer(
                amount, term, new BigDecimal("50000.00"), expectedRateWithInsuranceAndSalary, true, true
        );
        LoanOfferDto insuranceOffer = StubGenerator.createLoanOffer(
                amount, term, new BigDecimal("50000.00"), expectedRateWithInsurance, true, false
        );
        LoanOfferDto salaryOffer = StubGenerator.createLoanOffer(
                amount, term, new BigDecimal("50000.00"), expectedRateWithSalary, false, true
        );
        LoanOfferDto noneOffer = StubGenerator.createLoanOffer(
                amount, term, new BigDecimal("50000.00"), expectedRateNone, false, false
        );

        when(checkValueService.createSalaryAndInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryAndInsuranceOffer);
        when(checkValueService.createInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(insuranceOffer);
        when(checkValueService.createSalaryLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryOffer);
        when(checkValueService.createDefaultLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(noneOffer);

        List<LoanOfferDto> result = loanOfferService.createLoanOffers(amount, term);

        assertAll("Проверка ставок для разных комбинаций",
                () -> assertEquals(expectedRateWithInsuranceAndSalary, result.get(0).getRate()),
                () -> assertEquals(expectedRateWithInsurance, result.get(1).getRate()),
                () -> assertEquals(expectedRateWithSalary, result.get(2).getRate()),
                () -> assertEquals(expectedRateNone, result.get(3).getRate())
        );
    }

    private static Stream<Arguments> provideAmountAndTermScenarios() {
        return Stream.of(
                Arguments.of(
                        new BigDecimal("100000"), 6,
                        new BigDecimal("17.00"), // base - insurance - salary
                        new BigDecimal("18.00"), // base - insurance
                        new BigDecimal("19.00"), // base - salary
                        new BigDecimal("20.00")  // base
                ),
                Arguments.of(
                        new BigDecimal("500000"), 12,
                        new BigDecimal("17.00"),
                        new BigDecimal("18.00"),
                        new BigDecimal("19.00"),
                        new BigDecimal("20.00")
                ),
                Arguments.of(
                        new BigDecimal("1000000"), 24,
                        new BigDecimal("17.00"),
                        new BigDecimal("18.00"),
                        new BigDecimal("19.00"),
                        new BigDecimal("20.00")
                ),
                Arguments.of(
                        new BigDecimal("2000000"), 36,
                        new BigDecimal("17.00"),
                        new BigDecimal("18.00"),
                        new BigDecimal("19.00"),
                        new BigDecimal("20.00")
                )
        );
    }

    @Test
    @DisplayName("Проверка расчета общей суммы со страховкой")
    void createLoanOffers_WithInsurance_TotalAmountShouldIncludeInsurance() {
        BigDecimal insuranceAmount = baseAmount
                .multiply(insuranceRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
        BigDecimal totalWithInsurance = baseAmount.add(insuranceAmount);

        LoanOfferDto insuranceOfferWithTotal = StubGenerator.createLoanOfferWithTotal(
                baseAmount,
                totalWithInsurance,
                baseTerm,
                expectedMonthlyPayment,
                baseRate.subtract(insuranceRate),
                true,
                false
        );

        when(checkValueService.createInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(insuranceOfferWithTotal);
        when(checkValueService.createSalaryAndInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryAndInsuranceOffer);
        when(checkValueService.createSalaryLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryOffer);
        when(checkValueService.createDefaultLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(noneOffer);

        List<LoanOfferDto> result = loanOfferService.createLoanOffers(baseAmount, baseTerm);

        // Проверяем, что в предложении со страховкой общая сумма больше запрошенной
        assertTrue(result.get(1).getTotalAmount().compareTo(result.get(1).getRequestedAmount()) > 0);
        assertEquals(totalWithInsurance, result.get(1).getTotalAmount());

        assertEquals(result.get(2).getRequestedAmount(), result.get(2).getTotalAmount());
        assertEquals(result.get(3).getRequestedAmount(), result.get(3).getTotalAmount());
    }

    @Test
    @DisplayName("Проверка уникальности statementId для каждого предложения")
    void createLoanOffers_AllOffersHaveUniqueStatementId() {
        when(checkValueService.createSalaryAndInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryAndInsuranceOffer);
        when(checkValueService.createInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(insuranceOffer);
        when(checkValueService.createSalaryLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(salaryOffer);
        when(checkValueService.createDefaultLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(noneOffer);

        List<LoanOfferDto> result = loanOfferService.createLoanOffers(baseAmount, baseTerm);

        assertEquals(4, result.stream()
                .map(LoanOfferDto::getStatementId)
                .distinct()
                .count());
    }

    @Test
    @DisplayName("Проверка создания предложений с нулевой суммой")
    void createLoanOffers_WithZeroAmount() {
        BigDecimal zeroAmount = BigDecimal.ZERO;

        LoanOfferDto zeroOffer = StubGenerator.createLoanOffer(
                zeroAmount, baseTerm, BigDecimal.ZERO, baseRate, false, false
        );

        when(checkValueService.createDefaultLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(zeroOffer);
        when(checkValueService.createSalaryAndInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(zeroOffer);
        when(checkValueService.createInsuranceLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(zeroOffer);
        when(checkValueService.createSalaryLoanOffer(any(BigDecimal.class), any(Integer.class)))
                .thenReturn(zeroOffer);

        List<LoanOfferDto> result = loanOfferService.createLoanOffers(zeroAmount, baseTerm);

        assertAll("Проверка предложений с нулевой суммой",
                () -> assertEquals(4, result.size()),
                () -> result.forEach(offer -> {
                    assertEquals(BigDecimal.ZERO, offer.getRequestedAmount());
                    assertEquals(BigDecimal.ZERO, offer.getTotalAmount());
                    assertEquals(BigDecimal.ZERO, offer.getMonthlyPayment());
                })
        );
    }
}