package app.gozenko.DealMS.service;

import app.gozenko.DealMS.utils.StubGenerator;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.StatementStatusHistoryDto;
import app.gozenko.entity.Credit;
import app.gozenko.entity.Statement;
import app.gozenko.enums.StatementStatus;
import app.gozenko.enums.StatusChangeType;
import app.gozenko.repository.StatementRepository;
import app.gozenko.service.StatementServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementServiceImplTest {

    @Mock
    private StatementRepository statementRepository;

    @InjectMocks
    private StatementServiceImpl statementService;

    private Statement savedStatement;
    private LoanOfferDto loanOffer;
    private Credit credit;

    @BeforeEach
    void setUp() {

        savedStatement = StubGenerator.createStatement();
        loanOffer = StubGenerator.createLoanOffer(
                new BigDecimal("1000000"),
                12,
                new BigDecimal("92635.22"),
                new BigDecimal("20.00"),
                false,
                false);
        loanOffer.setStatementId(savedStatement.getId());
        credit = StubGenerator.createCredit();
    }

    @Test
    @DisplayName("Успешное создание заявления")
    void createStatement_Success() {
        when(statementRepository.save(any(Statement.class))).thenReturn(savedStatement);

        Statement result = statementService.createStatement(StubGenerator.createClient());

        assertAll("Проверка созданного заявления",
                () -> assertNotNull(result),
                () -> assertEquals(savedStatement.getId(), result.getId()),
                () -> assertEquals(StatementStatus.PREAPPROVAL, result.getStatus()),
                () -> assertNotNull(result.getCreationDate()),
                () -> assertNotNull(result.getStatusHistory()),
                () -> assertFalse(result.getStatusHistory().isEmpty()),
                () -> assertEquals(StatementStatus.PREAPPROVAL, result.getStatusHistory().getFirst().getStatus()),
                () -> assertEquals(StatusChangeType.AUTOMATIC, result.getStatusHistory().getFirst().getChangeType()),
                () -> assertNotNull(result.getSesCode()),
                () -> assertTrue(result.getSesCode() >= 0 && result.getSesCode() <= 101)
        );

        verify(statementRepository).save(any(Statement.class));
    }

    @Test
    @DisplayName("Поиск заявления по ID - успешно")
    void findById_Success() {
        UUID statementId = UUID.randomUUID();
        when(statementRepository.findById(statementId)).thenReturn(Optional.of(savedStatement));

        Statement result = statementService.findById(statementId);

        assertNotNull(result);
        assertEquals(savedStatement.getId(), result.getId());
        assertEquals(savedStatement.getClient(), result.getClient());
        verify(statementRepository).findById(statementId);
    }

    @Test
    @DisplayName("Поиск заявления по ID - заявление не найдено, выбрасывается исключение")
    void findById_StatementNotFound_ThrowsEntityNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(statementRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> statementService.findById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Не найдено заявление"));
        assertTrue(exception.getMessage().contains(nonExistentId.toString()));
        verify(statementRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Обновление заявления при выборе предложения - успешно")
    void updateStatement_Success() {
        when(statementRepository.findById(loanOffer.getStatementId())).thenReturn(Optional.of(savedStatement));
        when(statementRepository.save(any(Statement.class))).thenReturn(savedStatement);

        statementService.updateStatement(loanOffer);

        ArgumentCaptor<Statement> statementCaptor = ArgumentCaptor.forClass(Statement.class);
        verify(statementRepository).save(statementCaptor.capture());

        Statement updatedStatement = statementCaptor.getValue();

        assertAll("Проверка обновленного заявления",
                () -> assertEquals(StatementStatus.APPROVED, updatedStatement.getStatus()),
                () -> assertNotNull(updatedStatement.getAppliedOffer()),
                () -> assertEquals(loanOffer.getRequestedAmount(), updatedStatement.getAppliedOffer().getRequestedAmount()),
                () -> assertEquals(loanOffer.getTerm(), updatedStatement.getAppliedOffer().getTerm()),
                () -> assertTrue(updatedStatement.getStatusHistory().size() > 1)
        );
    }

    @Test
    @DisplayName("Обновление заявления - заявление не найдено, выбрасывается исключение")
    void updateStatement_StatementNotFound_ThrowsEntityNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        LoanOfferDto nonExistentLoanOffer = LoanOfferDto.builder()
                .statementId(nonExistentId)
                .build();

        when(statementRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> statementService.updateStatement(nonExistentLoanOffer)
        );

        assertTrue(exception.getMessage().contains("Не найдено заявление"));
        verify(statementRepository, never()).save(any(Statement.class));
    }

    @Test
    @DisplayName("Обновление статуса истории заявления - успешно")
    void updateStatementStatusHistory_Success() {
        Statement statement = StubGenerator.createStatement();
        int initialHistorySize = statement.getStatusHistory().size();

        statementService.updateStatementStatusHistory(statement, StatementStatus.CC_APPROVED);

        assertAll("Проверка обновления истории статусов",
                () -> assertEquals(StatementStatus.CC_APPROVED, statement.getStatus()),
                () -> assertNotNull(statement.getSignDate()),
                () -> assertEquals(initialHistorySize + 1, statement.getStatusHistory().size()),
                () -> assertEquals(StatementStatus.CC_APPROVED,
                        statement.getStatusHistory().get(initialHistorySize).getStatus()),
                () -> assertEquals(StatusChangeType.AUTOMATIC,
                        statement.getStatusHistory().get(initialHistorySize).getChangeType())
        );
    }

    @Test
    @DisplayName("Обновление статуса истории заявления - статус не CC_APPROVED, signDate не устанавливается")
    void updateStatementStatusHistory_NotCcApproved_SignDateNotSet() {
        Statement statement = StubGenerator.createStatement();
        LocalDateTime oldSignDate = statement.getSignDate();
        int initialHistorySize = statement.getStatusHistory().size();

        statementService.updateStatementStatusHistory(statement, StatementStatus.APPROVED);

        assertAll("Проверка обновления истории статусов",
                () -> assertEquals(StatementStatus.APPROVED, statement.getStatus()),
                () -> assertEquals(oldSignDate, statement.getSignDate()),
                () -> assertEquals(initialHistorySize + 1, statement.getStatusHistory().size()),
                () -> assertEquals(StatementStatus.APPROVED,
                        statement.getStatusHistory().get(initialHistorySize).getStatus())
        );
    }

    @Test
    @DisplayName("Добавление кредита к заявлению - успешно")
    void addCredit_Success() {
        Statement statement = StubGenerator.createStatement();
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        statementService.addCredit(statement, credit);

        ArgumentCaptor<Statement> statementCaptor = ArgumentCaptor.forClass(Statement.class);
        verify(statementRepository).save(statementCaptor.capture());

        Statement updatedStatement = statementCaptor.getValue();

        assertNotNull(updatedStatement.getCredit());
        assertEquals(credit.getId(), updatedStatement.getCredit().getId());
        assertEquals(credit.getAmount(), updatedStatement.getCredit().getAmount());
        assertEquals(credit.getTerm(), updatedStatement.getCredit().getTerm());
    }

    @Test
    @DisplayName("Обновление заявления - проверка сохранения истории статусов")
    void updateStatement_StatusHistoryUpdated() {
        when(statementRepository.findById(loanOffer.getStatementId())).thenReturn(Optional.of(savedStatement));
        when(statementRepository.save(any(Statement.class))).thenReturn(savedStatement);

        statementService.updateStatement(loanOffer);

        ArgumentCaptor<Statement> statementCaptor = ArgumentCaptor.forClass(Statement.class);
        verify(statementRepository).save(statementCaptor.capture());

        Statement capturedStatement = statementCaptor.getValue();
        List<StatementStatusHistoryDto> history = capturedStatement.getStatusHistory();

        assertNotNull(history);
        assertTrue(history.size() >= 2);
        assertEquals(StatementStatus.PREAPPROVAL, history.getFirst().getStatus());
        assertEquals(StatementStatus.APPROVED, history.getLast().getStatus());
    }

    @Test
    @DisplayName("Создание заявления - проверка генерации SES кода")
    void createStatement_SesCodeGeneration() {
        when(statementRepository.save(any(Statement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Statement result = statementService.createStatement(StubGenerator.createClient());

        assertNotNull(result.getSesCode());
        assertTrue(result.getSesCode() >= 0 && result.getSesCode() <= 101);
        verify(statementRepository).save(any(Statement.class));
    }

    @Test
    @DisplayName("Обновление статуса истории заявления - несколько обновлений")
    void updateStatementStatusHistory_MultipleUpdates() {
        Statement statement = StubGenerator.createStatement();
        int initialHistorySize = statement.getStatusHistory().size();

        statementService.updateStatementStatusHistory(statement, StatementStatus.APPROVED);
        statementService.updateStatementStatusHistory(statement, StatementStatus.CC_APPROVED);
        statementService.updateStatementStatusHistory(statement, StatementStatus.DOCUMENT_CREATED);

        assertAll("Проверка нескольких обновлений",
                () -> assertEquals(StatementStatus.DOCUMENT_CREATED, statement.getStatus()),
                () -> assertEquals(initialHistorySize + 3, statement.getStatusHistory().size())
        );
    }
}