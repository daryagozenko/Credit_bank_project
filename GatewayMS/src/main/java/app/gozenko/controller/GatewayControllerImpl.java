package app.gozenko.controller;

import app.gozenko.controller.interfaces.GatewayController;
import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.service.interfaces.DealCallingService;
import app.gozenko.service.interfaces.StatementCallingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gateway")
@Tag(name = "GatewayController")
public class GatewayControllerImpl implements GatewayController {

    private StatementCallingService statementCallingService;
    private DealCallingService dealCallingService;

    @PostMapping("/statement")
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
    @Override
    public ResponseEntity<List<LoanOfferDto>> calcConditionOfCredit(LoanStatementRequestDto loanState) {
        log.info("Input data in calcConditionOfCredit loanState-{}", loanState);
        List<LoanOfferDto> offers = statementCallingService.getLoanOffers(loanState);
        log.info("Successful create list of loanOffers-{}", offers);

        return ResponseEntity.status(HttpStatus.OK)
                .body(offers);
    }

    @PostMapping("/offer")
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
    @Override
    public ResponseEntity<Void> selectLoanOffer(LoanOfferDto loanOffer) {
        log.info("Input data in selectLoanOffer loanOffer-{}", loanOffer);
        statementCallingService.selectLoanOffer(loanOffer);
        log.info("Successful select loan offer");
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

        dealCallingService.calculateCredit(statementId, finishRegistration);

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
    public ResponseEntity<Void> sendDocuments(UUID statementId) {
        log.info("Input data in sendDocuments id-{}", statementId);

        dealCallingService.sendDocuments(statementId);

        log.info("Successfully sending document");
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
    public ResponseEntity<Void> signDocuments(UUID statementId) {
        log.info("Input data in signDocuments id-{}", statementId);

        dealCallingService.signDocuments(statementId);

        log.info("Successfully signing document");
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
    public ResponseEntity<Void> verifyCode(UUID statementId, Integer code) {
        log.info("Input data in verifyCode id-{}, code-{}", statementId, code);

        dealCallingService.verifyCode(statementId, code);

        log.info("Successfully verifying code");
        return ResponseEntity.ok().build();
    }
}
