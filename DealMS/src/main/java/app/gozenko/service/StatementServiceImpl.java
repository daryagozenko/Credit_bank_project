package app.gozenko.service;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.StatementStatusHistoryDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Credit;
import app.gozenko.entity.Statement;
import app.gozenko.enums.StatementStatus;
import app.gozenko.enums.StatusChangeType;
import app.gozenko.repository.StatementRepository;
import app.gozenko.service.interfaces.StatementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementServiceImpl implements StatementService {

    private static final Integer MAX_SES_CODE = 9999;
    private static final Integer MIN_SES_CODE = 1000;

    private final StatementRepository statementRepository;

    @Override
    public Statement createStatement(Client client) {
        List<StatementStatusHistoryDto> statusHistory = createStatusHistory();
        log.debug("statusHistory-{}", statusHistory);

        Statement statement = Statement.builder()
                .status(statusHistory.getLast().getStatus())
                .client(client)
                .statusHistory(statusHistory)
                .creationDate(LocalDateTime.now())
                .build();
        log.info("Statement saved");
        return statementRepository.save(statement);
    }

    @Transactional
    @Override
    public void updateStatementSesCode(Statement statement) {
        log.info("input: statement-{}", statement);
        int sesCode = MIN_SES_CODE + (int)(Math.random() * (MAX_SES_CODE - MIN_SES_CODE + 1));
        statement.setSesCode(sesCode);
        log.info("Statement ses code updated");
        statementRepository.save(statement);
    }

    @Transactional
    @Override
    public Statement findById(UUID statementId) {
        log.debug("input: statementId-{}", statementId);
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено заявление: " + statementId));
    }

    @Transactional
    @Override
    public void updateStatement(LoanOfferDto loanOffer) {
        UUID statementId = loanOffer.getStatementId();
        log.debug("updateStatement: statementId-{}", statementId);

        Statement statement = findByIdWithLock(statementId);
        log.debug("updateStatement: statement-{}", statement);

        List<StatementStatusHistoryDto> history = statement.getStatusHistory();
        log.debug("updateStatement: statusHistory-{}", history);
        history.add(addNewStatus(StatementStatus.APPROVED));
        statement.setStatusHistory(history);
        statement.setStatus(StatementStatus.APPROVED);
        statement.setAppliedOffer(loanOffer);
        log.debug("updateStatement: statement past update-{}", statement);

        log.info("Save statement");
        statementRepository.save(statement);
    }

    @Transactional
    @Override
    public void updateStatementStatusHistory(Statement statement, StatementStatus status) {
        Statement actual = statementRepository.findByIdWithLock(statement.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдено заявление: " + statement.getId()));
        List<StatementStatusHistoryDto> history = actual.getStatusHistory();
        history.add(addNewStatus(status));
        log.debug("statement statusHistory-{}", history);
        actual.setStatusHistory(history);

        if (status.equals(StatementStatus.CC_APPROVED)) {
            actual.setSignDate(LocalDateTime.now());
        }
        actual.setStatus(status);
        log.debug("statement-{}", actual);

        log.info("Save statement in update history");
        statementRepository.save(actual);
    }

    @Transactional
    @Override
    public void addCredit(Statement statement, Credit credit) {
        statement.setCredit(credit);
        log.debug("addCredit: statement-{}", statement);
        statementRepository.save(statement);
    }

    @Transactional
    public Statement findByIdWithLock(UUID statementId) {
        log.debug("Input: statementId-{}", statementId);
        return statementRepository.findByIdWithLock(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено заявление: " + statementId));
    }

    @Transactional
    public List<Statement> findAllStatements() {
        log.debug("Find all statements");
        return statementRepository.findAll();
    }

    private List<StatementStatusHistoryDto> createStatusHistory() {
        List<StatementStatusHistoryDto> statusHistory = new ArrayList<>();
        statusHistory.add(StatementStatusHistoryDto.builder()
                .status(StatementStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(StatusChangeType.AUTOMATIC)
                .build());
        log.debug(" createStatusHistory: statementHistory-{}", statusHistory);
        return statusHistory;
    }

    private StatementStatusHistoryDto addNewStatus(StatementStatus status) {
        log.debug("input: status-{}", status);
        return StatementStatusHistoryDto.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(StatusChangeType.AUTOMATIC)
                .build();
    }
}
