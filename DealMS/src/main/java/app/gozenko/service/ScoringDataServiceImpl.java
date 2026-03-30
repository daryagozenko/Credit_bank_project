package app.gozenko.service;

import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Statement;
import app.gozenko.service.interfaces.ScoringDataService;
import org.springframework.stereotype.Service;

@Service
public class ScoringDataServiceImpl implements ScoringDataService {

    @Override
    public ScoringDataDto createScoringData(FinishRegistrationRequestDto finishRegistration, Statement statement) {
        Client client = statement.getClient();
        return fillScoringData(client, statement, finishRegistration);

    }

    private ScoringDataDto fillScoringData(Client client,
                                           Statement statement,
                                           FinishRegistrationRequestDto finishRegistration) {
        return ScoringDataDto.builder()
                .amount(statement.getAppliedOffer().getRequestedAmount())
                .term(statement.getAppliedOffer().getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .gender(finishRegistration.getGender())
                .birthday(client.getBirthday())
                .passportSeries(client.getPassport().getSeries())
                .passportNumber(client.getPassport().getNumber())
                .passportIssueDate(finishRegistration.getPassportIssueDate())
                .passportIssueBranch(finishRegistration.getPassportIssueBranch())
                .maritalStatus(finishRegistration.getMaritalStatus())
                .dependentAmount(finishRegistration.getDependentAmount())
                .employment(finishRegistration.getEmployment())
                .accountNumber(finishRegistration.getAccountNumber())
                .isInsuranceEnabled(statement.getAppliedOffer().getIsInsuranceEnabled())
                .isSalaryClient(statement.getAppliedOffer().getIsSalaryClient())
                .build();
    }
}
