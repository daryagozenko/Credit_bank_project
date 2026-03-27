package app.gozenko.service;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Statement;
import app.gozenko.enums.StatementStatus;
import app.gozenko.repository.StatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementServiceImpl {

    private final StatementRepository statementRepository;

    public Statement createStatement(Client client, List<LoanOfferDto> loanOffers){
        Statement statement = Statement.builder()
                .id(UUID.randomUUID())
                .status(StatementStatus.PREAPPROVAL)
                .client(client)
                .build();
        List<LoanOfferDto> loanOffersBindStatement = loanOffers.stream()
                .peek(offer -> offer.setStatementId(statement.getId()))
                .toList();

        statement.setAppliedOffer(loanOffersBindStatement);
        return statementRepository.save(statement);
    }

}
