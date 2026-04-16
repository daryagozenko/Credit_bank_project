package app.gozenko.calculator.service;

import app.gozenko.calculator.utils.StubGenerator;
import app.gozenko.dto.CreditDto;
import app.gozenko.dto.PaymentScheduleElementDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.service.CalcCreditValueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CalcCreditValueServiceTest {

    @InjectMocks
    private CalcCreditValueService calcCreditValueService;

    private BigDecimal amount;
    private Integer term;
    private BigDecimal rate;
    private BigDecimal expectedMonthlyPayment;
    private ScoringDataDto baseScoringData;

    @BeforeEach
    void setUp() {
        calcCreditValueService = new CalcCreditValueService(BigDecimal.valueOf(100));
        amount = new BigDecimal("1000000");
        term = 12;
        rate = new BigDecimal("20.00");
        expectedMonthlyPayment = new BigDecimal("92634.51");
        baseScoringData = StubGenerator.createValidScoringRequest();
    }

    @Test
    @DisplayName("Проверка расчета ежемесячного платежа")
    void calcMonthlyPayment_Success() {
        BigDecimal result = calcCreditValueService.calcMonthlyPayment(rate, amount, term);

        assertAll("Проверка ежемесячного платежа",
                () -> assertNotNull(result),
                () -> assertEquals(0, result.compareTo(expectedMonthlyPayment),
                        "Ожидалось: " + expectedMonthlyPayment + ", но было: " + result),
                () -> assertEquals(2, result.scale())
        );
    }

    @ParameterizedTest
    @MethodSource("provideMonthlyPaymentScenarios")
    @DisplayName("Проверка расчета ежемесячного платежа для разных параметров")
    void calcMonthlyPayment_DifferentScenarios(
            BigDecimal rate,
            BigDecimal amount,
            Integer term,
            BigDecimal expectedPayment) {

        if (rate.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        BigDecimal result = calcCreditValueService.calcMonthlyPayment(rate, amount, term);

        assertEquals(0, result.compareTo(expectedPayment),
                String.format("Для rate=%s, amount=%s, term=%d. Ожидалось: %s, но было: %s",
                        rate, amount, term, expectedPayment, result));
    }

    private static Stream<Arguments> provideMonthlyPaymentScenarios() {
        return Stream.of(
                Arguments.of(new BigDecimal("20.00"), new BigDecimal("1000000"), 12, new BigDecimal("92634.51")),
                Arguments.of(new BigDecimal("15.00"), new BigDecimal("1000000"), 12, new BigDecimal("90258.31")),
                Arguments.of(new BigDecimal("10.00"), new BigDecimal("1000000"), 12, new BigDecimal("87915.89")),
                Arguments.of(new BigDecimal("20.00"), new BigDecimal("1000000"), 6, new BigDecimal("176522.78")),
                Arguments.of(new BigDecimal("20.00"), new BigDecimal("1000000"), 24, new BigDecimal("50895.80")),
                Arguments.of(new BigDecimal("20.00"), new BigDecimal("1000000"), 36, new BigDecimal("37163.58")),
                Arguments.of(new BigDecimal("20.00"), new BigDecimal("500000"), 12, new BigDecimal("46317.25")),
                Arguments.of(new BigDecimal("20.00"), new BigDecimal("2000000"), 12, new BigDecimal("185269.01")),
                Arguments.of(new BigDecimal("20.00"), new BigDecimal("10000"), 12, new BigDecimal("926.35"))
        );
    }

    @Test
    @DisplayName("Проверка создания графика платежей")
    void createPaymentSchedule_Success() {
        List<PaymentScheduleElementDto> schedule = calcCreditValueService.createPaymentSchedule(
                amount, term, rate, expectedMonthlyPayment);

        assertAll("Проверка графика платежей",
                () -> assertNotNull(schedule),
                () -> assertEquals(term, schedule.size()),

                () -> {
                    PaymentScheduleElementDto first = schedule.getFirst();
                    assertEquals(1, first.getNumber());
                    assertNotNull(first.getDate());
                    assertEquals(0, expectedMonthlyPayment.compareTo(first.getTotalPayment()));
                    assertTrue(first.getInterestPayment().compareTo(BigDecimal.ZERO) > 0);
                    assertTrue(first.getDebtPayment().compareTo(BigDecimal.ZERO) > 0);
                    assertTrue(first.getRemainingDebt().compareTo(BigDecimal.ZERO) > 0);
                },

                () -> {
                    PaymentScheduleElementDto last = schedule.get(term - 1);
                    assertEquals(term, last.getNumber());
                    assertEquals(0, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).compareTo(last.getRemainingDebt()));
                }
        );
    }

    @Test
    @DisplayName("Проверка корректности расчета остатка долга")
    void createPaymentSchedule_RemainingDebtCalculation() {
        List<PaymentScheduleElementDto> schedule = calcCreditValueService.createPaymentSchedule(
                amount, term, rate, expectedMonthlyPayment);

        BigDecimal totalPaid = schedule.stream()
                .map(PaymentScheduleElementDto::getDebtPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(0, amount.setScale(2, RoundingMode.HALF_UP).compareTo(totalPaid),
                "Ожидалось: " + amount + ", но выплачено: " + totalPaid);
    }

    @Test
    @DisplayName("Проверка расчета ПСК")
    void calcPsk_Success() {
        List<PaymentScheduleElementDto> schedule = calcCreditValueService.createPaymentSchedule(
                amount, term, rate, expectedMonthlyPayment);

        BigDecimal psk = calcCreditValueService.calcPsk(amount, schedule, term);

        assertAll("Проверка ПСК",
                () -> assertNotNull(psk),
                () -> assertTrue(psk.compareTo(BigDecimal.ZERO) > 0),
                () -> assertEquals(2, psk.scale())
        );
    }

    @ParameterizedTest
    @MethodSource("providePskScenarios")
    @DisplayName("Проверка расчета ПСК для разных сценариев")
    void calcPsk_DifferentScenarios(
            BigDecimal amount,
            Integer term,
            BigDecimal rate,
            BigDecimal expectedPskMin,
            BigDecimal expectedPskMax) {

        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(rate, amount, term);
        List<PaymentScheduleElementDto> schedule = calcCreditValueService.createPaymentSchedule(
                amount, term, rate, monthlyPayment);

        BigDecimal psk = calcCreditValueService.calcPsk(amount, schedule, term);

        assertTrue(psk.compareTo(expectedPskMin) >= 0 && psk.compareTo(expectedPskMax) <= 0,
                String.format("ПСК %s должна быть между %s и %s", psk, expectedPskMin, expectedPskMax));
    }

    private static Stream<Arguments> providePskScenarios() {
        return Stream.of(
                Arguments.of(new BigDecimal("1000000"), 12, new BigDecimal("20.00"),
                        new BigDecimal("22.00"), new BigDecimal("23.00")), // Исправлено на реальные значения
                Arguments.of(new BigDecimal("1000000"), 24, new BigDecimal("15.00"),
                        new BigDecimal("16.00"), new BigDecimal("17.00")), // Исправлено на реальные значения
                Arguments.of(new BigDecimal("500000"), 36, new BigDecimal("10.00"),
                        new BigDecimal("10.00"), new BigDecimal("11.00"))
        );
    }

    @Test
    @DisplayName("Проверка mainCounting - полный расчет кредита")
    void mainCounting_Success() {
        CreditDto result = calcCreditValueService.mainCounting(baseScoringData, rate);

        assertAll("Проверка полного расчета кредита",
                () -> assertNotNull(result),
                () -> assertEquals(0, baseScoringData.getAmount().compareTo(result.getAmount())),
                () -> assertEquals(baseScoringData.getTerm(), result.getTerm()),
                () -> assertEquals(0, expectedMonthlyPayment.compareTo(result.getMonthlyPayment()),
                        "Ожидалось: " + expectedMonthlyPayment + ", но было: " + result.getMonthlyPayment()),
                () -> assertEquals(0, rate.compareTo(result.getRate())),
                () -> assertNotNull(result.getPsk()),
                () -> assertEquals(baseScoringData.getIsInsuranceEnabled(), result.getIsInsuranceEnabled()),
                () -> assertEquals(baseScoringData.getIsSalaryClient(), result.getIsSalaryClient()),
                () -> assertNotNull(result.getPaymentSchedule()),
                () -> assertEquals(baseScoringData.getTerm(), result.getPaymentSchedule().size())
        );
    }

    @Test
    @DisplayName("Проверка mainCounting с разными ставками")
    void mainCounting_DifferentRates() {
        BigDecimal[] rates = {new BigDecimal("15.00"), new BigDecimal("20.00"), new BigDecimal("25.00")};

        for (BigDecimal testRate : rates) {
            CreditDto result = calcCreditValueService.mainCounting(baseScoringData, testRate);

            assertEquals(0, testRate.compareTo(result.getRate()));
            assertTrue(result.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Test
    @DisplayName("Проверка создания графика для одного месяца")
    void createPaymentSchedule_OneMonthTerm() {
        term = 1;
        BigDecimal monthlyPayment = calcCreditValueService.calcMonthlyPayment(rate, amount, term);

        List<PaymentScheduleElementDto> schedule = calcCreditValueService.createPaymentSchedule(
                amount, term, rate, monthlyPayment);

        assertAll("Проверка для одного месяца",
                () -> assertEquals(1, schedule.size()),
                () -> {
                    PaymentScheduleElementDto element = schedule.getFirst();
                    assertEquals(1, element.getNumber());
                    BigDecimal totalPayment = element.getInterestPayment().add(element.getDebtPayment());
                    assertEquals(0, totalPayment.compareTo(monthlyPayment));
                    assertEquals(0, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).compareTo(element.getRemainingDebt()));
                }
        );
    }

    @Test
    @DisplayName("Проверка обработки нулевой суммы")
    void createPaymentSchedule_WithZeroAmount() {
        amount = BigDecimal.ZERO;
        expectedMonthlyPayment = BigDecimal.ZERO;

        List<PaymentScheduleElementDto> schedule = calcCreditValueService.createPaymentSchedule(
                amount, term, rate, expectedMonthlyPayment);

        assertAll("Проверка с нулевой суммой",
                () -> assertNotNull(schedule),
                () -> {
                    PaymentScheduleElementDto first = schedule.getFirst();
                    assertEquals(0, BigDecimal.ZERO.compareTo(first.getTotalPayment()));
                    assertEquals(0, BigDecimal.ZERO.compareTo(first.getInterestPayment()));
                    assertEquals(0, BigDecimal.ZERO.compareTo(first.getDebtPayment()));
                    assertEquals(0, BigDecimal.ZERO.compareTo(first.getRemainingDebt()));
                }
        );
    }

    @Test
    @DisplayName("Проверка валидности дат в графике платежей")
    void createPaymentSchedule_DateValidation() {
        LocalDate startDate = LocalDate.now();

        List<PaymentScheduleElementDto> schedule = calcCreditValueService.createPaymentSchedule(
                amount, term, rate, expectedMonthlyPayment);

        for (int i = 0; i < schedule.size(); i++) {
            PaymentScheduleElementDto element = schedule.get(i);
            LocalDate expectedDate = startDate.plusMonths(i + 1);

            assertEquals(expectedDate, element.getDate(),
                    String.format("Дата для платежа %d должна быть %s", i + 1, expectedDate));
        }
    }

    @Test
    @DisplayName("Проверка с разными данными из StubGenerator")
    void mainCounting_WithDifferentScoringData() {
        ScoringDataDto differentRequest = StubGenerator.createScoringRequestWithAge(25);
        differentRequest.setAmount(new BigDecimal("500000"));
        differentRequest.setTerm(24);

        BigDecimal testRate = new BigDecimal("15.00");
        CreditDto result = calcCreditValueService.mainCounting(differentRequest, testRate);

        assertEquals(0, new BigDecimal("500000").compareTo(result.getAmount()));
        assertEquals(24, result.getTerm());
        assertEquals(0, testRate.compareTo(result.getRate()));
    }
}