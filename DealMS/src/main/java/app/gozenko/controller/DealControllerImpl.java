package app.gozenko.controller;

import app.gozenko.controller.interfaces.DealController;
import app.gozenko.dto.CreditDto;
import app.gozenko.dto.EmailMessageDto;
import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.dto.StatementResponseDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Credit;
import app.gozenko.entity.Statement;
import app.gozenko.enums.EmailTheme;
import app.gozenko.enums.StatementStatus;
import app.gozenko.exception.CalculatorClientException;
import app.gozenko.exception.NotVerifyCodeException;
import app.gozenko.service.AdminService;
import app.gozenko.service.CalculatorCallingService;
import app.gozenko.service.ClientServiceImpl;
import app.gozenko.service.CreditServiceImpl;
import app.gozenko.service.EmailServiceImpl;
import app.gozenko.service.ScoringDataServiceImpl;
import app.gozenko.service.StatementServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/deal")
@RequiredArgsConstructor
@Tag(name = "DealController")
public class DealControllerImpl implements DealController {

    private static final String SERVICE_SOURCE = "gateway";
    private static final String CALCULATE_PATH = "/calculate/{statementId}";
    private static final String DOCUMENT_SEND_PATH = "/document/{statementId}/send";
    private static final String DOCUMENT_SIGN_PATH = "/document/{statementId}/sign";
    private static final String DOCUMENT_CODE_PATH = "/document/{statementId}/code/{code}";

    private static final String selectLoanOfferEmailText = "Вы успешно выбрали кредитное предложение\n" +
            "Перейдите по ссылке для финального рассчета кредита: %s%s".formatted(SERVICE_SOURCE, CALCULATE_PATH);
    private static final String calculateCreditEmailText = "Документы о сделке успешно созданы\n" +
            "Перейдите по ссылке для отправки документов: %s%s".formatted(SERVICE_SOURCE, DOCUMENT_SEND_PATH);
    private static final String sendDocumentsEmailText = "Документы о вашей сделке отправлены\n" +
            "Перейдите по ссылке для подписания документов: %s%s".formatted(SERVICE_SOURCE, DOCUMENT_SIGN_PATH);
    private static final String signDocumentsEmailText = "Вам отправлен код подтверждения: ";
    private static final String signDocumentsLinkEmailText = "\n Перейдите по ссылке для подтверждения сделки: " +
            "%s%s".formatted(SERVICE_SOURCE, DOCUMENT_CODE_PATH);
    private static final String verifyCodeEmailText = "Сделка подтверждена!";
    private static final String statementDeniedEmailText = "Сделка отклонена по причине: ";

    @Autowired
    private final KafkaTemplate<String, EmailMessageDto> kafkaTemplate;

    private final ClientServiceImpl clientService;
    private final StatementServiceImpl statementService;
    private final ScoringDataServiceImpl scoringDataService;
    private final CreditServiceImpl creditService;
    private final CalculatorCallingService calculatorCallingService;
    private final EmailServiceImpl emailService;
    private final AdminService adminService;

    @Value("${kafka.topic.finish-registration}")
    private String FINISH_REGISTRATION;
    @Value("${kafka.topic.create-documents}")
    private String CREATE_DOCUMENT;
    @Value("${kafka.topic.send-documents}")
    private String SEND_DOCUMENT;
    @Value("${kafka.topic.send-ses}")
    private String SEND_SES;
    @Value("${kafka.topic.credit-issued}")
    private String CREDIT_ISSUED;
    @Value("${kafka.topic.statement-denied}")
    private String STATEMENT_DENIED;

    @PostMapping("/statement")
    @Operation(summary = "расчет вариантов предложений по кредиту")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoanOfferDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры запроса",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            )})
    public ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(LoanStatementRequestDto loanState) {
        log.info("Input data in calcConditionOfCredit loanState-{}", loanState);

        Client client = clientService.createClient(loanState);
        log.debug("Result in calcConditionOfCredit client-{}", client);
        Statement statement = statementService.createStatement(client);
        log.debug("Result in calcConditionOfCredit statement-{}", statement);

        List<LoanOfferDto> offers = calculatorCallingService.getLoanOffers(loanState, statement.getId());
        log.info("Result in calcConditionOfCredit offers-{}", offers);

        return ResponseEntity.status(HttpStatus.OK)
                .body(offers);
    }

    @PostMapping("/offer/select")
    @Operation(summary = "выбор предложения по кредиту")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заявка не найдена"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )})
    public ResponseEntity<Void> selectLoanOffer(LoanOfferDto loanOffer) {
        log.info("Input data in selectLoanOffer loanOffer-{}", loanOffer);

        statementService.updateStatement(loanOffer);
        log.info("Statement in selectLoanOffer updated");

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.FINISH_REGISTRATION,
                loanOffer.getStatementId(),
                selectLoanOfferEmailText);
        log.debug("EmailMessage in selectLoanOffer-{}", dto);

        kafkaTemplate.send(FINISH_REGISTRATION,
                String.valueOf(loanOffer.getStatementId()), dto);
        log.info("Send to kafka topic {} in selectLoanOffer", FINISH_REGISTRATION);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "финальный рассчет кредитного предложения")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заявка не найдена"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )})
    public ResponseEntity<Void> calculateCredit(
            UUID statementId,
            FinishRegistrationRequestDto finishRegistration) {
        log.info("Input data in calculateCredit finishRegistration-{}, statementId-{}",
                finishRegistration, statementId);

        Statement statement = statementService.findById(statementId);
        log.debug("Result in calculateCredit statement-{}", statement);
        ScoringDataDto scoringData = scoringDataService.createScoringData(finishRegistration, statement);
        log.debug("Result in calculateCredit scoringData-{}", scoringData);

        try {
            CreditDto creditDto = calculatorCallingService.calcCredit(scoringData);
            log.debug("Result in calculateCredit creditDto-{}", creditDto);

            clientService.updateClient(statement, finishRegistration);

            Credit credit = creditService.createCredit(creditDto);
            log.debug("Result in calculateCredit credit-{}", credit);

            statementService.updateStatementStatusHistory(statement, StatementStatus.CC_APPROVED);
            statementService.addCredit(statement, credit);
            log.debug("Updated statement-{}", statement);
        } catch (CalculatorClientException ex) {
            statementService.updateStatementStatusHistory(statement, StatementStatus.CC_DENIED);
            log.debug("Updated statement status-{}", statement.getStatus());
            EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.STATEMENT_DENIED,
                    statementId,
                    statementDeniedEmailText + ex.getMessage());
            log.debug("EmailMessage in calculateCredit-{}", dto);

            kafkaTemplate.send(STATEMENT_DENIED,
                    String.valueOf(statementId), dto);
            log.info("Send to kafka topic {} in calculateCredit", STATEMENT_DENIED);

            throw new CalculatorClientException(ex.getMessage());
        }

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.CREATE_DOCUMENTS,
                statementId,
                calculateCreditEmailText);
        log.debug("EmailMessage in calculateCredit-{}", dto);

        kafkaTemplate.send(CREATE_DOCUMENT,
                String.valueOf(statementId), dto);
        log.info("Send to kafka topic {} in calculateCredit", CREATE_DOCUMENT);

        log.info("Credit in calculateCredit created");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/{statementId}/send")
    @Operation(summary = "запрос на отправку документов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заявка не найдена"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )})
    @Override
    public ResponseEntity<Void> sendDocuments(UUID statementId) {
        log.info("Input data in sendDocuments id-{}", statementId);

        Statement statement = statementService.findById(statementId);
        log.debug("Statement in sendDocuments-{}", statement);

        statementService.updateStatementStatusHistory(statement, StatementStatus.PREPARE_DOCUMENTS);
        log.debug("Set status-{}", statement.getStatus());

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.SEND_DOCUMENTS,
                statementId,
                sendDocumentsEmailText);
        log.debug("EmailMessage in sendDocuments-{}", dto);

        kafkaTemplate.send(SEND_DOCUMENT,
                String.valueOf(statementId), dto);
        log.info("Send to kafka topic {} in sendDocuments", SEND_DOCUMENT);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/{statementId}/sign")
    @Operation(summary = "запрос на подписание документов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заявка не найдена"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )})
    @Override
    public ResponseEntity<Void> signDocuments(UUID statementId) {
        log.info("Input data in signDocuments id-{}", statementId);

        Statement statement = statementService.findById(statementId);
        log.info("Statement in signDocuments-{}", statement);

        statementService.updateStatementSesCode(statement);
        log.debug("Set ses code in statement");

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.SEND_SES_CODE,
                statementId,
                signDocumentsEmailText + statement.getSesCode() + signDocumentsLinkEmailText);
        log.debug("EmailMessage in signDocuments-{}", dto);

        kafkaTemplate.send(SEND_SES,
                String.valueOf(statementId), dto);
        log.info("Send to kafka topic {} in signDocuments", SEND_SES);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/{statementId}/code/{code}")
    @Operation(summary = "подписание документов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заявка не найдена"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )})
    @Override
    public ResponseEntity<Void> verifyCode(UUID statementId, Integer code) {
        log.info("Input data in verifyCode id-{}, code-{}", statementId, code);

        Statement statement = statementService.findById(statementId);
        log.debug("Statement in verifyCode-{}", statement);

        if (!code.equals(statement.getSesCode())) {
            throw new NotVerifyCodeException("Код верификации не совпадает");
        }

        statementService.updateStatementStatusHistory(statement, StatementStatus.DOCUMENT_SIGNED);
        statementService.updateStatementStatusHistory(statement, StatementStatus.CREDIT_ISSUED);
        log.debug("Set status history-{}", statement.getStatusHistory());

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.CREDIT_ISSUED,
                statementId,
                verifyCodeEmailText);
        log.debug("EmailMessage in verifyCode-{}", dto);

        kafkaTemplate.send(CREDIT_ISSUED,
                String.valueOf(statementId), dto);
        log.info("Send to kafka topic {} in verifyCode", CREDIT_ISSUED);

        return ResponseEntity.ok().build();
    }

    @Tag(name = "Admin control board", description = "API для управления заявками администратором")
    @GetMapping("/admin/statement/{statementId}")
    @Operation(summary = "получение заявки по id (админский запрос)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заявка не найдена",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(hidden = true))
            )})
    @Override
    public ResponseEntity<StatementResponseDto> getStatement(UUID statementId) {
        log.info("Input data in getStatement id-{}", statementId);

        Statement statement = statementService.findById(statementId);
        log.debug("Statement-{}", statement);

        StatementResponseDto statementResponseDto = adminService.buildStatementResponse(statement);
        log.info("Result statement response-{}", statementResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(statementResponseDto);
    }

    @GetMapping("/admin/statement")
    @Operation(summary = "получение всех заявок (админский запрос)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заявки отсутствуют",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(hidden = true))
            )})
    @Override
    public ResponseEntity<List<StatementResponseDto>> getAllStatements() {
        log.info("Input getAllStatements");

        List<Statement> statements = statementService.findAllStatements();
        log.debug("All statements-{}", statements);

        List<StatementResponseDto> allStatementsResponseDto = adminService.getAllStatementsResponse(statements);
        log.info("Result statements response-{}", allStatementsResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(allStatementsResponseDto);
    }

    @PutMapping("/admin/statement/{statementId}/status")
    @Operation(summary = "обновление статуса заявки по id (админский запрос)")
    @Override
    public ResponseEntity<StatementResponseDto> putStatementStatus(UUID statementId) {
        log.info("Input statementId in putStatementStatus-{}", statementId);

        Statement statement = statementService.findById(statementId);
        statementService.updateStatementStatusHistory(statement, StatementStatus.DOCUMENT_CREATED);
        log.debug("Set status in put-{}", statement.getStatus());

        StatementResponseDto responseDto = adminService.buildStatementResponse(statement);
        log.info("Statement response-{}", responseDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
