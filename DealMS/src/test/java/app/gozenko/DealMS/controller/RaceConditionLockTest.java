package app.gozenko.DealMS.controller;

import app.gozenko.entity.Statement;
import app.gozenko.enums.StatementStatus;
import app.gozenko.repository.StatementRepository;
import app.gozenko.service.interfaces.StatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RaceConditionLockTest {

    private final StatementRepository repository;
    private final StatementService statementService;
    private Statement actual;
    private UUID statementId;

    @Autowired
    public RaceConditionLockTest(StatementRepository repository, StatementService statementService) {
        this.repository = repository;
        this.statementService = statementService;
    }

    @BeforeEach
    void init() {
        repository.deleteAllInBatch();
        actual = Statement.builder()
                .statusHistory(new ArrayList<>())
                .build();
        actual = repository.saveAndFlush(actual);
        statementId = actual.getId();
    }

    @Test
    @DisplayName("Обновление истории заявок двумя потоками")
    void checkLock() {
        CountDownLatch startCounter = new CountDownLatch(2);

        CompletableFuture<Void> firstThread = CompletableFuture.runAsync(() -> {
            startCounter.countDown();
            try {
                startCounter.await();
                Statement currStatement = statementService.findByIdWithLock(statementId);
                statementService.updateStatementStatusHistory(currStatement, StatementStatus.APPROVED);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> secondThread = CompletableFuture.runAsync(() -> {
            startCounter.countDown();
            try {
                startCounter.await();
                Statement currStatement = statementService.findByIdWithLock(statementId);
                statementService.updateStatementStatusHistory(currStatement, StatementStatus.CC_APPROVED);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> allThreads = CompletableFuture.allOf(firstThread, secondThread);

        assertDoesNotThrow(() -> allThreads.join());

        Statement updatedStatement = repository.findById(statementId)
                .orElseThrow(() -> new RuntimeException("Statement not found"));

        assertEquals(2, updatedStatement.getStatusHistory().size());
    }
}
