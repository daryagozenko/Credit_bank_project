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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class RaceConditionLockTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    private final StatementRepository repository;
    private final StatementService statementService;
    private UUID statementId;

    @Autowired
    public RaceConditionLockTest(StatementRepository repository, StatementService statementService) {
        this.repository = repository;
        this.statementService = statementService;
    }

    @BeforeEach
    void init() {
        repository.deleteAllInBatch();
        Statement actual = Statement.builder()
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

        assertDoesNotThrow(allThreads::join);

        Statement updatedStatement = repository.findById(statementId)
                .orElseThrow(() -> new RuntimeException("Statement not found"));

        assertEquals(2, updatedStatement.getStatusHistory().size());
    }
}
