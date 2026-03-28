package app.gozenko.controller;

import app.gozenko.controller.interfaces.DealController;
import app.gozenko.dto.*;
import app.gozenko.entity.Client;
import app.gozenko.entity.Credit;
import app.gozenko.entity.Statement;
import app.gozenko.enums.StatementStatus;
import app.gozenko.exception.UnloadedDataException;
import app.gozenko.service.ClientServiceImpl;
import app.gozenko.service.CreditServiceImpl;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/deal")
@RequiredArgsConstructor
@Tag(name = "DealController")
public class DealControllerImpl implements DealController {

    private final RestClient restClient;
    private final ClientServiceImpl clientService;
    private final StatementServiceImpl statementService;
    private final ScoringDataServiceImpl scoringDataService;
    private final CreditServiceImpl creditService;

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
    public ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(@RequestBody LoanStatementRequestDto loanState) {
        Client client = clientService.createClient(loanState);
        Statement statement = statementService.createStatement(client);

        List<LoanOfferDto> offers = restClient.post()
                .uri("/offers")
                .body(loanState)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {
                })
                .getBody();

        if (offers == null) throw new UnloadedDataException("Предложения не поступили");
        List<LoanOfferDto> loanOffersBindStatement = offers.stream()
                .peek(offer -> offer.setStatementId(statement.getId()))
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(loanOffersBindStatement);
    }

    @PostMapping("/offer/select")
    @Operation(summary = "выбор предложения по кредиту")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class))
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
    public ResponseEntity<String> selectLoanOffer(@RequestBody LoanOfferDto loanOffer) {
        statementService.updateStatement(loanOffer);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Select Loan Offer");
    }

    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "финальный рассчет кредитного предложения")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class))
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
    public ResponseEntity<String> calculateCredit(
            @PathVariable("statementId") UUID statementId,
            @RequestBody FinishRegistrationRequestDto finishRegistration) {
        Statement statement = statementService.findById(statementId);
        log.info("Result in selectLoanOffer statement-{}", statement);
        ScoringDataDto scoringData = scoringDataService.createScoringData(finishRegistration, statement);
        log.info("Result in selectLoanOffer scoringData-{}", scoringData);

        CreditDto creditDto = restClient.post()
                .uri("/calc")
                .body(scoringData)
                .retrieve()
                .body(CreditDto.class);
        log.info("Result in selectLoanOffer creditDto-{}", creditDto);

        clientService.updateClient(statement, finishRegistration);

        if (creditDto == null) throw new UnloadedDataException("Кредит не поступил");
        Credit credit = creditService.createCredit(creditDto);
        log.info("Result in selectLoanOffer credit-{}", credit);
        statementService.updateStatementStatusHistory(statement, StatementStatus.CC_APPROVED);
        statementService.addCredit(statement, credit);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Done calculate credit: "+credit.getId());
    }
}
