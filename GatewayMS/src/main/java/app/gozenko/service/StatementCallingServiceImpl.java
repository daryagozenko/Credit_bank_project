package app.gozenko.service;

import app.gozenko.client.StatementClient;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.service.interfaces.StatementCallingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementCallingServiceImpl implements StatementCallingService {

    private final StatementClient statementClient;

    @Override
    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState) {
        log.info("Input loanState-{}", loanState);
        List<LoanOfferDto> offers = statementClient.getLoanOffers(loanState);
        log.info("Result offers-{}", offers);

        return offers;
    }

    @Override
    public void selectLoanOffer(LoanOfferDto loanOffer){
        log.info("Input loanOffer - {}", loanOffer);
        statementClient.selectOffer(loanOffer);
        log.info("Selecting done");
    }
}
