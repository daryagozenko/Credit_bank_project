package app.gozenko.service;

import app.gozenko.client.DealClient;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealCallingService {

    private final DealClient dealClient;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState) {
        log.info("Input loanState - {}", loanState);
        List<LoanOfferDto> offers = dealClient.getLoanOffers(loanState);
        log.debug("getLoanOffers: offers-{}", offers);

        return offers;
    }

    public void selectLoanOffer(LoanOfferDto loanOffer){
        log.info("Input loanOffer - {}", loanOffer);
        dealClient.selectOffer(loanOffer);
        log.info("Selecting done");
    }
}