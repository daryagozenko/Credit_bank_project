package app.gozenko.service;

import app.gozenko.client.CalculatorClient;
import app.gozenko.dto.CreditDto;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.exception.UnloadedDataException;
import app.gozenko.service.interfaces.CalculatorCallingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorCallingServiceImpl implements CalculatorCallingService {

    private final CalculatorClient calculatorClient;

    @Override
    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanState, UUID statementId) {
        List<LoanOfferDto> offers = calculatorClient.getLoanOffers(loanState);
        log.debug("getLoanOffers: offers-{}", offers);

        return offers.stream()
                .map(offer -> {
                    offer.setStatementId(statementId);
                    return offer;
                })
                .toList();
    }

    @Override
    public CreditDto calcCredit(ScoringDataDto scoringData) {
        CreditDto creditDto = calculatorClient.getCredit(scoringData);
        log.debug("calcCredit: creditDto-{}", creditDto);
        if (creditDto == null) {
            throw new UnloadedDataException("Кредит не поступил");
        }
        return creditDto;
    }
}
