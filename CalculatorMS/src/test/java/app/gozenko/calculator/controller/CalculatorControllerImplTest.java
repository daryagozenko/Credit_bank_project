package app.gozenko.calculator.controller;

import app.gozenko.calculator.utils.StubGenerator;
import app.gozenko.controller.CalculatorControllerImpl;
import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.exception.ValidationDataException;
import app.gozenko.service.interfaces.LoanOfferService;
import app.gozenko.service.interfaces.PreScoringService;
import app.gozenko.service.interfaces.ScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorControllerImplTest {

    @Mock
    private PreScoringService preScoringService;

    @Mock
    private LoanOfferService loanOfferService;

    @Mock
    private ScoringService scoringService;

    @InjectMocks
    private CalculatorControllerImpl calculatorController;

    private LoanStatementRequestDto validLoanStatement;
    private ScoringDataDto validScoringData;
    private List<LoanOfferDto> expectedLoanOffers;
    private CreditDto expectedCreditDto;

    @BeforeEach
    void setUp() {
        BigDecimal amount = new BigDecimal("300000");
        int term = 12;

        validLoanStatement = StubGenerator.createValidLoanStatementRequest();
        validScoringData = StubGenerator.createValidScoringRequest();

        LoanOfferDto offer1 = StubGenerator.createLoanOffer(
                amount, term, new BigDecimal("27790.57"), new BigDecimal("17.00"), true, true
        );
        LoanOfferDto offer2 = StubGenerator.createLoanOffer(
                amount, term, new BigDecimal("27900.00"), new BigDecimal("18.00"), true, false
        );
        LoanOfferDto offer3 = StubGenerator.createLoanOffer(
                amount, term, new BigDecimal("28000.00"), new BigDecimal("19.00"), false, true
        );
        LoanOfferDto offer4 = StubGenerator.createLoanOffer(
                amount, term, new BigDecimal("28100.00"), new BigDecimal("20.00"), false, false
        );

        expectedLoanOffers = Arrays.asList(offer1, offer2, offer3, offer4);

        expectedCreditDto = StubGenerator.createExpectedCreditDto(
                validScoringData.getAmount(),
                validScoringData.getTerm(),
                new BigDecimal("92634.51"),
                new BigDecimal("20.00"),
                true,
                true
        );
    }

    @Test
    @DisplayName("Успешное получение кредитных предложений")
    void calcConditionOfCredit_Success() {
        doNothing().when(preScoringService).preScoringLoan(any(LoanStatementRequestDto.class));
        doReturn(expectedLoanOffers)
                .when(loanOfferService)
                .createLoanOffers(any(BigDecimal.class), any(Integer.class));

        ResponseEntity<List<LoanOfferDto>> response = calculatorController.calcConditionOfCredit(validLoanStatement);

        assertAll("Проверка успешного ответа",
                () -> assertNotNull(response),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(4, response.getBody().size())
        );

        verify(preScoringService).preScoringLoan(validLoanStatement);
        verify(loanOfferService).createLoanOffers(validLoanStatement.getAmount(), validLoanStatement.getTerm());
    }

    @Test
    @DisplayName("Проверка передачи правильных параметров в сервисы")
    void calcConditionOfCredit_VerifyParameters() {
        doNothing().when(preScoringService).preScoringLoan(any(LoanStatementRequestDto.class));
        doReturn(expectedLoanOffers)
                .when(loanOfferService)
                .createLoanOffers(any(BigDecimal.class), any(Integer.class));

        calculatorController.calcConditionOfCredit(validLoanStatement);

        verify(preScoringService).preScoringLoan(validLoanStatement);
        verify(loanOfferService).createLoanOffers(validLoanStatement.getAmount(), validLoanStatement.getTerm());
    }

    @Test
    @DisplayName("Проверка порядка предложений в ответе")
    void calcConditionOfCredit_VerifyOrder() {
        doNothing().when(preScoringService).preScoringLoan(any(LoanStatementRequestDto.class));
        doReturn(expectedLoanOffers)
                .when(loanOfferService)
                .createLoanOffers(any(BigDecimal.class), any(Integer.class));

        ResponseEntity<List<LoanOfferDto>> response = calculatorController.calcConditionOfCredit(validLoanStatement);

        List<?> offers = response.getBody();
        assertNotNull(offers);

        LoanOfferDto firstOffer = (LoanOfferDto) offers.get(0);
        LoanOfferDto secondOffer = (LoanOfferDto) offers.get(1);
        LoanOfferDto thirdOffer = (LoanOfferDto) offers.get(2);
        LoanOfferDto fourthOffer = (LoanOfferDto) offers.get(3);

        assertAll("Проверка порядка предложений",
                () -> assertTrue(firstOffer.getIsInsuranceEnabled() && firstOffer.getIsSalaryClient()),
                () -> assertTrue(secondOffer.getIsInsuranceEnabled() && !secondOffer.getIsSalaryClient()),
                () -> assertTrue(!thirdOffer.getIsInsuranceEnabled() && thirdOffer.getIsSalaryClient()),
                () -> assertTrue(!fourthOffer.getIsInsuranceEnabled() && !fourthOffer.getIsSalaryClient())
        );
    }

    @Test
    @DisplayName("Ошибка валидации при получении предложений")
    void calcConditionOfCredit_ValidationFailed() {
        String errorMessage = "Возраст должен быть больше 18";
        doThrow(new ValidationDataException(errorMessage))
                .when(preScoringService).preScoringLoan(any(LoanStatementRequestDto.class));

        assertThrows(ValidationDataException.class,
                () -> calculatorController.calcConditionOfCredit(validLoanStatement));

        verify(preScoringService).preScoringLoan(validLoanStatement);
        verify(loanOfferService, never()).createLoanOffers(any(), any());
    }

    @Test
    @DisplayName("Успешный расчет кредита")
    void validateAndCalc_Success() {
        when(scoringService.createScoringData(any(ScoringDataDto.class)))
                .thenReturn(expectedCreditDto);

        ResponseEntity<?> response = calculatorController.validateAndCalc(validScoringData);

        assertAll("Проверка успешного расчета кредита",
                () -> assertNotNull(response),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertTrue(response.getBody() instanceof CreditDto)
        );

        CreditDto result = (CreditDto) response.getBody();
        assertEquals(expectedCreditDto.getAmount(), result.getAmount());
        assertEquals(expectedCreditDto.getTerm(), result.getTerm());
        assertEquals(expectedCreditDto.getRate(), result.getRate());

        verify(scoringService).createScoringData(validScoringData);
    }

    @Test
    @DisplayName("Проверка передачи правильных параметров в scoringService")
    void validateAndCalc_VerifyParameters() {
        when(scoringService.createScoringData(any(ScoringDataDto.class)))
                .thenReturn(expectedCreditDto);

        calculatorController.validateAndCalc(validScoringData);

        verify(scoringService).createScoringData(validScoringData);
    }

    @Test
    @DisplayName("Ошибка скоринга при расчете кредита")
    void validateAndCalc_ScoringFailed() {
        String errorMessage = "Безработный";
        doThrow(new ValidationDataException(errorMessage))
                .when(scoringService).createScoringData(any(ScoringDataDto.class));

        assertThrows(ValidationDataException.class,
                () -> calculatorController.validateAndCalc(validScoringData));

        verify(scoringService).createScoringData(validScoringData);
    }

    @Test
    @DisplayName("Проверка с разными суммами в LoanStatementRequest")
    void calcConditionOfCredit_DifferentAmounts() {
        BigDecimal[] amounts = {new BigDecimal("100000"), new BigDecimal("500000"), new BigDecimal("1000000")};

        for (BigDecimal testAmount : amounts) {
            LoanStatementRequestDto request = StubGenerator.createLoanStatementRequestWithAmount(testAmount);

            doNothing().when(preScoringService).preScoringLoan(any(LoanStatementRequestDto.class));
            doReturn(expectedLoanOffers)
                    .when(loanOfferService)
                    .createLoanOffers(any(BigDecimal.class), any(Integer.class));

            ResponseEntity<List<LoanOfferDto>> response = calculatorController.calcConditionOfCredit(request);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            verify(preScoringService).preScoringLoan(request);
            verify(loanOfferService).createLoanOffers(testAmount, request.getTerm());

            reset(preScoringService, loanOfferService);
        }
    }

    @Test
    @DisplayName("Проверка с разными сроками в ScoringData")
    void validateAndCalc_DifferentTerms() {
        Integer[] terms = {6, 12, 24, 36};

        for (Integer testTerm : terms) {
            ScoringDataDto request = StubGenerator.createValidScoringRequest();
            request.setTerm(testTerm);

            when(scoringService.createScoringData(any(ScoringDataDto.class)))
                    .thenReturn(expectedCreditDto);

            ResponseEntity<?> response = calculatorController.validateAndCalc(request);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            verify(scoringService).createScoringData(request);

            reset(preScoringService, scoringService);
        }
    }


    @Test
    @DisplayName("Проверка возвращаемого статуса для разных сценариев")
    void validateAndCalc_ResponseStatus() {
        when(scoringService.createScoringData(any(ScoringDataDto.class)))
                .thenReturn(expectedCreditDto);

        ResponseEntity<?> response = calculatorController.validateAndCalc(validScoringData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}