package app.gozenko.controller;

import app.gozenko.controller.interfaces.CalculatorController;
import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.service.interfaces.LoanOfferService;
import app.gozenko.service.interfaces.PreScoringService;
import app.gozenko.service.interfaces.ScoringService;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/calculator")
@Tag(name = "CalculatorController")
public class CalculatorControllerImpl implements CalculatorController {

    private final PreScoringService preScoringService;
    private final LoanOfferService loanOfferService;
    private final ScoringService scoringService;


    @PostMapping("/offers")
    @Operation(summary = "создание кредитной заявки")
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
        log.debug("Sending a LoanStatementRequest to preScoringService");
        preScoringService.preScoringLoan(loanState);

        log.info("Successful create list of loanOffers");
        return ResponseEntity.status(HttpStatus.OK)
                .body(loanOfferService.createLoanOffers(
                        loanState.getAmount(),
                        loanState.getTerm()));
    }

    @PostMapping("/calc")
    @Operation(summary = "расчет полной стоимости кредита и графика платежей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreditDto.class))
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
    public ResponseEntity<CreditDto> validateAndCalc(ScoringDataDto scoringData) {
        log.info("Input data in validateAndCalc scoringData-{}", scoringData);

        log.info("Successful create credit offer");
        return ResponseEntity.status(HttpStatus.OK)
                .body(scoringService.createScoringData(scoringData));
    }
}
