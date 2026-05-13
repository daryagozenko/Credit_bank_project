package app.gozenko.controller;

import app.gozenko.controller.interfaces.StatementController;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.service.DealCallingService;
import app.gozenko.service.interfaces.PreScoringService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statement")
public class StatementControllerImpl implements StatementController {

    private final DealCallingService dealCallingService;
    private final PreScoringService preScoringService;

    @PostMapping("/")
    @Override
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
        log.debug("Sending a LoanStatementRequest to preScoringService");
        preScoringService.preScoringLoan(loanState);

        log.info("Successful create list of loanOffers");

        return ResponseEntity.status(HttpStatus.OK)
                .body(dealCallingService.getLoanOffers(loanState));
    }

    @PostMapping("/offer")
    @Override
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
    public ResponseEntity<Void> selectLoanOffer(LoanOfferDto loanOffer) {
        log.info("Input data in selectLoanOffer loanOffer-{}", loanOffer);
        dealCallingService.selectLoanOffer(loanOffer);
        log.info("Successful select loan offer");
        return ResponseEntity.ok().build();
    }
}
