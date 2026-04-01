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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementServiceImpl implements StatementService {

    private static final Integer MAX_SES_CODE = 101;

    private final StatementRepository statementRepository;

    private List<StatementStatusHistoryDto> statusHistory;

    @Override
    public Statement createStatement(Client client) {
        statusHistory = createStatusHistory();

        Statement statement = Statement.builder()
                .status(statusHistory.getLast().getStatus())
                .client(client)
                .statusHistory(statusHistory)
                .sesCode((int) (Math.random() * MAX_SES_CODE))
                .creationDate(LocalDateTime.now())
                .build();

        return statementRepository.save(statement);
    }

    @Override
    public Statement findById(UUID statementId){
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено заявление: " + statementId));
    }

    @Transactional
    @Override
    public void updateStatement(LoanOfferDto loanOffer) {
        UUID statementId = loanOffer.getStatementId();

        Statement statement = findById(statementId);

        List<StatementStatusHistoryDto> history = statement.getStatusHistory();
        history.add(addNewStatus(StatementStatus.APPROVED));
        statement.setStatusHistory(history);
        statement.setStatus(StatementStatus.APPROVED);
        statement.setAppliedOffer(loanOffer);

        statementRepository.save(statement);
    }

    @Override
    public void updateStatementStatusHistory(Statement statement, StatementStatus status){
        List<StatementStatusHistoryDto> history = statement.getStatusHistory();
        history.add(addNewStatus(status));
        statement.setStatusHistory(history);

        if(status.equals(StatementStatus.CC_APPROVED)){
            statement.setSignDate(LocalDateTime.now());
        }
        statement.setStatus(status);
    }

    @Override
    public void addCredit(Statement statement, Credit credit){
        statement.setCredit(credit);
        statementRepository.save(statement);
    }

    private List<StatementStatusHistoryDto> createStatusHistory() {
        statusHistory = new ArrayList<>();
        statusHistory.add(StatementStatusHistoryDto.builder()
                .status(StatementStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(StatusChangeType.AUTOMATIC)
                .build());

        return statusHistory;
    }

    private StatementStatusHistoryDto addNewStatus(StatementStatus status){
        return StatementStatusHistoryDto.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(StatusChangeType.AUTOMATIC)
                .build();
    }


}
