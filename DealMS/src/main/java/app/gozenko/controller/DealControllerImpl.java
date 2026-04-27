package app.gozenko.controller;

import app.gozenko.controller.interfaces.DealController;
import app.gozenko.dto.CreditDto;
import app.gozenko.dto.EmailMessageDto;
import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Credit;
import app.gozenko.entity.Statement;
import app.gozenko.enums.EmailTheme;
import app.gozenko.enums.StatementStatus;
import app.gozenko.exception.CalculatorClientException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    private final KafkaTemplate<String, EmailMessageDto> kafkaTemplate;

    private final ClientServiceImpl clientService;
    private final StatementServiceImpl statementService;
    private final ScoringDataServiceImpl scoringDataService;
    private final CreditServiceImpl creditService;
    private final CalculatorCallingService calculatorCallingService;
    private final EmailServiceImpl emailService;

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
    public ResponseEntity<Void> selectLoanOffer(LoanOfferDto loanOffer) {
        log.info("Input data in selectLoanOffer loanOffer-{}", loanOffer);

        statementService.updateStatement(loanOffer);
        log.info("Statement in selectLoanOffer updated");

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.FINISH_REGISTRATION,
                loanOffer.getStatementId(),
                "Вы успешно выбрали кредитное предложение");
        log.debug("EmailMessage in selectLoanOffer-{}", dto);

        kafkaTemplate.send(FINISH_REGISTRATION,
                String.valueOf(loanOffer.getStatementId()), dto);
        log.info("Send to kafka in selectLoanOffer");

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
                    description = "Неверные параметры запроса",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Сущность не найдена в бд",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
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
        } catch (CalculatorClientException ex) {
            EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.STATEMENT_DENIED,
                    statementId,
                    "Сделка отклонена по причине: " + ex.getMessage());
            log.debug("EmailMessage in calculateCredit-{}", dto);

            kafkaTemplate.send(STATEMENT_DENIED,
                    String.valueOf(statementId), dto);
            log.info("Send to kafka in calculateCredit");

            throw new CalculatorClientException(ex.getMessage());
        }

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.CREATE_DOCUMENTS,
                statementId,
                "Документы о сделке успешно созданы");
        log.debug("EmailMessage in calculateCredit-{}", dto);

        kafkaTemplate.send(CREATE_DOCUMENT,
                String.valueOf(statementId), dto);
        log.info("Send to kafka in calculateCredit");

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
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )})
    public ResponseEntity<Void> requestToSendDocuments(@PathVariable("statementId") UUID statementId) {
        log.info("Input data in requestToSendDocuments id-{}", statementId);

        Statement statement = statementService.findById(statementId);
        log.info("Statement in requestToSendDocuments-{}", statement);

        statementService.updateStatementStatusHistory(statement, StatementStatus.PREPARE_DOCUMENTS);
        log.debug("Set status PREPARE_DOCUMENTS");

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.SEND_DOCUMENTS,
                statementId,
                "Документы о вашей сделке отправлены");
        log.debug("EmailMessage in requestToSendDocuments-{}", dto);

        kafkaTemplate.send(SEND_DOCUMENT,
                String.valueOf(statementId), dto);
        log.info("Send to kafka in requestToSendDocuments");

        statementService.updateStatementStatusHistory(statement, StatementStatus.DOCUMENT_CREATED);
        log.debug("Set status DOCUMENT_CREATED");

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
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )})
    public ResponseEntity<Void> requestToSignDocuments(@PathVariable("statementId") UUID statementId) {
        log.info("Input data in requestToSignDocuments id-{}", statementId);

        Statement statement = statementService.findById(statementId);
        log.info("Statement in requestToSignDocuments-{}", statement);

        statementService.updateStatementSesCode(statement);
        log.debug("Set ses code in statement");

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.SEND_SES_CODE,
                statementId,
                "Вам отправлен код подтверждения: " + statement.getSesCode());
        log.debug("EmailMessage in requestToSignDocuments-{}", dto);

        kafkaTemplate.send(SEND_SES,
                String.valueOf(statementId), dto);
        log.info("Send to kafka in requestToSignDocuments");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/{statementId}/code")
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
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )})
    public ResponseEntity<Void> requestToVerifyCode(@PathVariable("statementId") UUID statementId) {
        log.info("Input data in requestToVerifyCode id-{}", statementId);

        Statement statement = statementService.findById(statementId);
        log.info("Statement in requestToVerifyCode-{}", statement);

        statementService.updateStatementStatusHistory(statement, StatementStatus.DOCUMENT_SIGNED);
        statementService.updateStatementStatusHistory(statement, StatementStatus.CREDIT_ISSUED);
        log.debug("Set status PREPARE_DOCUMENTS and CREDIT_ISSUED");
        log.info("Statement paste update history-{}", statement);

        EmailMessageDto dto = emailService.createEmailMessage(EmailTheme.CREDIT_ISSUED,
                statementId,
                "Сделка подтверждена!");
        log.debug("EmailMessage in requestToVerifyCode-{}", dto);

        kafkaTemplate.send(CREDIT_ISSUED,
                String.valueOf(statementId), dto);
        log.info("Send to kafka in requestToVerifyCode");

        return ResponseEntity.ok().build();
    }
}
