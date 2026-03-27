package app.gozenko.controller;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Statement;
import app.gozenko.exception.UnloadedDataException;
import app.gozenko.service.ClientServiceImpl;
import app.gozenko.service.StatementServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deal")
@RequiredArgsConstructor
@Tag(name = "DealController")
public class DealControllerImpl {

    private final RestClient restClient;
    private final ClientServiceImpl clientService;
    private final StatementServiceImpl statementService;

    @PostMapping("/statement")
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
    public ResponseEntity<String> selectLoanOffer(@RequestBody LoanOfferDto loanOffer) {
        Statement statement = statementService.updateStatement(loanOffer);
        return ResponseEntity.ok(statement.getId().toString());
    }


}
