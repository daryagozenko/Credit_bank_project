package app.gozenko.DealMS.controller;

import app.gozenko.DealMS.utils.StubGenerator;
import app.gozenko.controller.DealControllerImpl;
import app.gozenko.dto.*;
import app.gozenko.entity.Client;
import app.gozenko.entity.Credit;
import app.gozenko.entity.Statement;
import app.gozenko.enums.StatementStatus;
import app.gozenko.service.*;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealControllerImplTest {

    @Mock
    private ClientServiceImpl clientService;

    @Mock
    private StatementServiceImpl statementService;

    @Mock
    private ScoringDataServiceImpl scoringDataService;

    @Mock
    private CreditServiceImpl creditService;

    @Mock
    private CalculatorCallingService calculatorCallingService;

    @InjectMocks
    private DealControllerImpl dealController;

    private LoanStatementRequestDto validLoanStatement;
    private FinishRegistrationRequestDto validFinishRegistration;
    private Client savedClient;
    private Statement savedStatement;
    private List<LoanOfferDto> expectedLoanOffers;
    private ScoringDataDto expectedScoringData;
    private CreditDto expectedCreditDto;
    private Credit savedCredit;
    private UUID statementId;

    @BeforeEach
    void setUp() {
        statementId = UUID.randomUUID();

        validLoanStatement = StubGenerator.createValidLoanStatementRequest();
        validFinishRegistration = StubGenerator.createValidFinishRegistrationRequest();
        savedClient = StubGenerator.createClient();
        savedStatement = StubGenerator.createStatement(statementId, savedClient);
        expectedLoanOffers = StubGenerator.createLoanOffersList();
        expectedScoringData = StubGenerator.createValidScoringData();
        expectedCreditDto = StubGenerator.createCreditDto();
        savedCredit = StubGenerator.createCredit();
    }

    // ==== calcConditionOfCredit ====

    @Test
    @DisplayName("Успешный расчет предложений по кредиту")
    void calcConditionOfCredit_Success() {
        when(clientService.createClient(any(LoanStatementRequestDto.class))).thenReturn(savedClient);
        when(statementService.createStatement(any(Client.class))).thenReturn(savedStatement);
        when(calculatorCallingService.getLoanOffers(any(LoanStatementRequestDto.class), any(UUID.class)))
                .thenReturn(expectedLoanOffers);

        ResponseEntity<List<LoanOfferDto>> response = dealController.calcConditionOfCredit(validLoanStatement);

        assertAll("Проверка успешного ответа",
                () -> assertNotNull(response),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(4, response.getBody().size())
        );

        verify(clientService).createClient(validLoanStatement);
        verify(statementService).createStatement(savedClient);
        verify(calculatorCallingService).getLoanOffers(validLoanStatement, savedStatement.getId());
    }

    @Test
    @DisplayName("Проверка передачи правильных параметров при расчете предложений")
    void calcConditionOfCredit_VerifyParameters() {
        when(clientService.createClient(any(LoanStatementRequestDto.class))).thenReturn(savedClient);
        when(statementService.createStatement(any(Client.class))).thenReturn(savedStatement);
        when(calculatorCallingService.getLoanOffers(any(LoanStatementRequestDto.class), any(UUID.class)))
                .thenReturn(expectedLoanOffers);

        dealController.calcConditionOfCredit(validLoanStatement);

        verify(clientService).createClient(validLoanStatement);
        verify(statementService).createStatement(savedClient);
        verify(calculatorCallingService).getLoanOffers(validLoanStatement, savedStatement.getId());
    }

    @Test
    @DisplayName("Ошибка при создании клиента")
    void calcConditionOfCredit_ClientCreationFailed() {
        when(clientService.createClient(any(LoanStatementRequestDto.class)))
                .thenThrow(new RuntimeException("Ошибка создания клиента"));

        assertThrows(RuntimeException.class,
                () -> dealController.calcConditionOfCredit(validLoanStatement));

        verify(clientService).createClient(validLoanStatement);
        verify(statementService, never()).createStatement(any());
        verify(calculatorCallingService, never()).getLoanOffers(any(), any());
    }

    // ===== selectLoanOffer ====

    @Test
    @DisplayName("Успешный выбор предложения")
    void selectLoanOffer_Success() {
        LoanOfferDto loanOffer = expectedLoanOffers.getFirst();
        doNothing().when(statementService).updateStatement(any(LoanOfferDto.class));

        ResponseEntity<Void> response = dealController.selectLoanOffer(loanOffer);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(statementService).updateStatement(loanOffer);
    }

    @Test
    @DisplayName("Проверка передачи правильного предложения")
    void selectLoanOffer_VerifyParameters() {
        LoanOfferDto loanOffer = expectedLoanOffers.getFirst();
        doNothing().when(statementService).updateStatement(any(LoanOfferDto.class));

        dealController.selectLoanOffer(loanOffer);

        verify(statementService).updateStatement(loanOffer);
    }

    @Test
    @DisplayName("Ошибка при выборе предложения - заявление не найдено")
    void selectLoanOffer_StatementNotFound() {
        LoanOfferDto loanOffer = expectedLoanOffers.getFirst();
        doThrow(new RuntimeException("Не найдено заявление"))
                .when(statementService).updateStatement(any(LoanOfferDto.class));

        assertThrows(RuntimeException.class,
                () -> dealController.selectLoanOffer(loanOffer));

        verify(statementService).updateStatement(loanOffer);
    }

    // ===== calculateCredit ====

    @Test
    @DisplayName("Успешный финальный расчет кредита")
    void calculateCredit_Success() {
        when(statementService.findById(statementId)).thenReturn(savedStatement);
        when(scoringDataService.createScoringData(any(FinishRegistrationRequestDto.class), any(Statement.class)))
                .thenReturn(expectedScoringData);
        when(calculatorCallingService.calcCredit(any(ScoringDataDto.class)))
                .thenReturn(expectedCreditDto);
        doNothing().when(clientService).updateClient(any(Statement.class), any(FinishRegistrationRequestDto.class));
        when(creditService.createCredit(any(CreditDto.class))).thenReturn(savedCredit);
        doNothing().when(statementService).updateStatementStatusHistory(any(Statement.class), eq(StatementStatus.CC_APPROVED));
        doNothing().when(statementService).addCredit(any(Statement.class), any(Credit.class));

        ResponseEntity<Void> response = dealController.calculateCredit(statementId, validFinishRegistration);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(statementService).findById(statementId);
        verify(scoringDataService).createScoringData(validFinishRegistration, savedStatement);
        verify(calculatorCallingService).calcCredit(expectedScoringData);
        verify(clientService).updateClient(savedStatement, validFinishRegistration);
        verify(creditService).createCredit(expectedCreditDto);
        verify(statementService).updateStatementStatusHistory(savedStatement, StatementStatus.CC_APPROVED);
        verify(statementService).addCredit(savedStatement, savedCredit);
    }

    @Test
    @DisplayName("Проверка передачи правильных параметров при расчете кредита")
    void calculateCredit_VerifyParameters() {
        when(statementService.findById(statementId)).thenReturn(savedStatement);
        when(scoringDataService.createScoringData(any(FinishRegistrationRequestDto.class), any(Statement.class)))
                .thenReturn(expectedScoringData);
        when(calculatorCallingService.calcCredit(any(ScoringDataDto.class)))
                .thenReturn(expectedCreditDto);
        doNothing().when(clientService).updateClient(any(Statement.class), any(FinishRegistrationRequestDto.class));
        when(creditService.createCredit(any(CreditDto.class))).thenReturn(savedCredit);
        doNothing().when(statementService).updateStatementStatusHistory(any(Statement.class), eq(StatementStatus.CC_APPROVED));
        doNothing().when(statementService).addCredit(any(Statement.class), any(Credit.class));

        dealController.calculateCredit(statementId, validFinishRegistration);

        verify(statementService).findById(statementId);
        verify(scoringDataService).createScoringData(validFinishRegistration, savedStatement);
        verify(calculatorCallingService).calcCredit(expectedScoringData);
        verify(clientService).updateClient(savedStatement, validFinishRegistration);
        verify(creditService).createCredit(expectedCreditDto);
        verify(statementService).updateStatementStatusHistory(savedStatement, StatementStatus.CC_APPROVED);
        verify(statementService).addCredit(savedStatement, savedCredit);
    }

    @Test
    @DisplayName("Ошибка - заявление не найдено при расчете кредита")
    void calculateCredit_StatementNotFound() {
        when(statementService.findById(statementId)).thenThrow(new RuntimeException("Не найдено заявление"));

        assertThrows(RuntimeException.class,
                () -> dealController.calculateCredit(statementId, validFinishRegistration));

        verify(statementService).findById(statementId);
        verify(scoringDataService, never()).createScoringData(any(), any());
        verify(calculatorCallingService, never()).calcCredit(any());
        verify(clientService, never()).updateClient(any(), any());
        verify(creditService, never()).createCredit(any());
        verify(statementService, never()).updateStatementStatusHistory(any(), any());
        verify(statementService, never()).addCredit(any(), any());
    }

    @Test
    @DisplayName("Ошибка при создании скоринговых данных")
    void calculateCredit_ScoringDataCreationFailed() {
        when(statementService.findById(statementId)).thenReturn(savedStatement);
        when(scoringDataService.createScoringData(any(FinishRegistrationRequestDto.class), any(Statement.class)))
                .thenThrow(new RuntimeException("Ошибка создания скоринговых данных"));

        assertThrows(RuntimeException.class,
                () -> dealController.calculateCredit(statementId, validFinishRegistration));

        verify(statementService).findById(statementId);
        verify(scoringDataService).createScoringData(validFinishRegistration, savedStatement);
        verify(calculatorCallingService, never()).calcCredit(any());
        verify(clientService, never()).updateClient(any(), any());
        verify(creditService, never()).createCredit(any());
        verify(statementService, never()).updateStatementStatusHistory(any(), any());
        verify(statementService, never()).addCredit(any(), any());
    }

    @Test
    @DisplayName("Ошибка при расчете кредита в калькуляторе")
    void calculateCredit_CalculatorCallFailed() {
        when(statementService.findById(statementId)).thenReturn(savedStatement);
        when(scoringDataService.createScoringData(any(FinishRegistrationRequestDto.class), any(Statement.class)))
                .thenReturn(expectedScoringData);
        when(calculatorCallingService.calcCredit(any(ScoringDataDto.class)))
                .thenThrow(new RuntimeException("Ошибка вызова калькулятора"));

        assertThrows(RuntimeException.class,
                () -> dealController.calculateCredit(statementId, validFinishRegistration));

        verify(statementService).findById(statementId);
        verify(scoringDataService).createScoringData(validFinishRegistration, savedStatement);
        verify(calculatorCallingService).calcCredit(expectedScoringData);
        verify(clientService, never()).updateClient(any(), any());
        verify(creditService, never()).createCredit(any());
        verify(statementService, never()).updateStatementStatusHistory(any(), any());
        verify(statementService, never()).addCredit(any(), any());
    }

    @Test
    @DisplayName("Ошибка при создании кредита")
    void calculateCredit_CreditCreationFailed() {
        when(statementService.findById(statementId)).thenReturn(savedStatement);
        when(scoringDataService.createScoringData(any(FinishRegistrationRequestDto.class), any(Statement.class)))
                .thenReturn(expectedScoringData);
        when(calculatorCallingService.calcCredit(any(ScoringDataDto.class)))
                .thenReturn(expectedCreditDto);
        doNothing().when(clientService).updateClient(any(Statement.class), any(FinishRegistrationRequestDto.class));
        when(creditService.createCredit(any(CreditDto.class)))
                .thenThrow(new RuntimeException("Ошибка создания кредита"));

        assertThrows(RuntimeException.class,
                () -> dealController.calculateCredit(statementId, validFinishRegistration));

        verify(statementService).findById(statementId);
        verify(scoringDataService).createScoringData(validFinishRegistration, savedStatement);
        verify(calculatorCallingService).calcCredit(expectedScoringData);
        verify(clientService).updateClient(savedStatement, validFinishRegistration);
        verify(creditService).createCredit(expectedCreditDto);
        verify(statementService, never()).updateStatementStatusHistory(any(), any());
        verify(statementService, never()).addCredit(any(), any());
    }

    @Test
    @DisplayName("Проверка возвращаемого статуса для разных сценариев")
    void calculateCredit_ResponseStatus() {
        when(statementService.findById(statementId)).thenReturn(savedStatement);
        when(scoringDataService.createScoringData(any(FinishRegistrationRequestDto.class), any(Statement.class)))
                .thenReturn(expectedScoringData);
        when(calculatorCallingService.calcCredit(any(ScoringDataDto.class)))
                .thenReturn(expectedCreditDto);
        doNothing().when(clientService).updateClient(any(Statement.class), any(FinishRegistrationRequestDto.class));
        when(creditService.createCredit(any(CreditDto.class))).thenReturn(savedCredit);
        doNothing().when(statementService).updateStatementStatusHistory(any(Statement.class), eq(StatementStatus.CC_APPROVED));
        doNothing().when(statementService).addCredit(any(Statement.class), any(Credit.class));

        ResponseEntity<Void> response = dealController.calculateCredit(statementId, validFinishRegistration);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Проверка с разными суммами в LoanStatementRequest")
    void calcConditionOfCredit_DifferentAmounts() {
        LoanStatementRequestDto request = StubGenerator.createValidLoanStatementRequest();

        when(clientService.createClient(any(LoanStatementRequestDto.class))).thenReturn(savedClient);
        when(statementService.createStatement(any(Client.class))).thenReturn(savedStatement);
        when(calculatorCallingService.getLoanOffers(any(LoanStatementRequestDto.class), any(UUID.class)))
                .thenReturn(expectedLoanOffers);

        ResponseEntity<List<LoanOfferDto>> response = dealController.calcConditionOfCredit(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(clientService).createClient(request);
        verify(statementService).createStatement(savedClient);
        verify(calculatorCallingService).getLoanOffers(request, savedStatement.getId());
    }

    @Test
    @DisplayName("Проверка с разными UUID заявлений")
    void calculateCredit_DifferentStatementIds() {
        UUID[] statementIds = {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};

        for (UUID testStatementId : statementIds) {
            Statement testStatement = StubGenerator.createStatement(testStatementId, savedClient);

            when(statementService.findById(testStatementId)).thenReturn(testStatement);
            when(scoringDataService.createScoringData(any(FinishRegistrationRequestDto.class), any(Statement.class)))
                    .thenReturn(expectedScoringData);
            when(calculatorCallingService.calcCredit(any(ScoringDataDto.class)))
                    .thenReturn(expectedCreditDto);
            doNothing().when(clientService).updateClient(any(Statement.class), any(FinishRegistrationRequestDto.class));
            when(creditService.createCredit(any(CreditDto.class))).thenReturn(savedCredit);
            doNothing().when(statementService).updateStatementStatusHistory(any(Statement.class), eq(StatementStatus.CC_APPROVED));
            doNothing().when(statementService).addCredit(any(Statement.class), any(Credit.class));

            ResponseEntity<Void> response = dealController.calculateCredit(testStatementId, validFinishRegistration);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            verify(statementService).findById(testStatementId);
            reset(statementService, scoringDataService, calculatorCallingService, clientService, creditService);
        }
    }
}