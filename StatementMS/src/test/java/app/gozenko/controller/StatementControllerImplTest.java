package app.gozenko.controller;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.service.DealCallingService;
import app.gozenko.service.interfaces.PreScoringService;
import app.gozenko.utils.StubGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementControllerImplTest {

    @Mock
    private DealCallingService dealCallingService;

    @Mock
    private PreScoringService preScoringService;

    @InjectMocks
    private StatementControllerImpl statementController;

    private LoanStatementRequestDto validLoanStatement;
    private LoanOfferDto validLoanOffer;
    private List<LoanOfferDto> validOffers;

    @BeforeEach
    void setUp() {
        validLoanStatement = StubGenerator.createValidLoanStatementRequest();
        validLoanOffer = StubGenerator.createValidLoanOffer();
        validOffers = StubGenerator.createValidOffers();
    }

    @Test
    @DisplayName("Успешный расчет предложений по кредиту")
    void calcConditionOfCredit_Success() {
        doNothing().when(preScoringService).preScoringLoan(any(LoanStatementRequestDto.class));
        when(dealCallingService.getLoanOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(validOffers);

        ResponseEntity<List<LoanOfferDto>> response = statementController.calcConditionOfCredit(validLoanStatement);

        assertAll("Проверка успешного ответа",
                () -> assertNotNull(response),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(4, response.getBody().size())
        );

        verify(preScoringService).preScoringLoan(validLoanStatement);
        verify(dealCallingService).getLoanOffers(validLoanStatement);
    }

    @Test
    @DisplayName("Ошибка при прескоринге - неверные параметры")
    void calcConditionOfCredit_PreScoringFailed() {
        doThrow(new RuntimeException("Ошибка прескоринга: неверные параметры"))
                .when(preScoringService).preScoringLoan(any(LoanStatementRequestDto.class));

        assertThrows(RuntimeException.class,
                () -> statementController.calcConditionOfCredit(validLoanStatement));

        verify(preScoringService).preScoringLoan(validLoanStatement);
        verify(dealCallingService, never()).getLoanOffers(any());
    }

    @Test
    @DisplayName("Ошибка при получении предложений от сервиса Deal")
    void calcConditionOfCredit_DealCallingFailed() {
        doNothing().when(preScoringService).preScoringLoan(any(LoanStatementRequestDto.class));
        when(dealCallingService.getLoanOffers(any(LoanStatementRequestDto.class)))
                .thenThrow(new RuntimeException("Ошибка вызова Deal сервиса"));

        assertThrows(RuntimeException.class,
                () -> statementController.calcConditionOfCredit(validLoanStatement));

        verify(preScoringService).preScoringLoan(validLoanStatement);
        verify(dealCallingService).getLoanOffers(validLoanStatement);
    }

    @Test
    @DisplayName("Успешный выбор предложения")
    void selectLoanOffer_Success() {
        doNothing().when(dealCallingService).selectLoanOffer(any(LoanOfferDto.class));

        ResponseEntity<Void> response = statementController.selectLoanOffer(validLoanOffer);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(dealCallingService).selectLoanOffer(validLoanOffer);
    }

    @Test
    @DisplayName("Ошибка при выборе предложения - сервис Deal недоступен")
    void selectLoanOffer_DealCallingFailed() {
        doThrow(new RuntimeException("Ошибка вызова Deal сервиса при выборе предложения"))
                .when(dealCallingService).selectLoanOffer(any(LoanOfferDto.class));

        assertThrows(RuntimeException.class,
                () -> statementController.selectLoanOffer(validLoanOffer));

        verify(dealCallingService).selectLoanOffer(validLoanOffer);
    }
}

