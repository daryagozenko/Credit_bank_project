package app.gozenko.service;

import app.gozenko.dto.ClientResponseDto;
import app.gozenko.dto.CreditResponseDto;
import app.gozenko.dto.StatementResponseDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Credit;
import app.gozenko.entity.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AdminService {

    public List<StatementResponseDto> getAllStatementsResponse(List<Statement> statements) {
        log.debug("Input statements-{}", statements);
        List<StatementResponseDto> result = new ArrayList<>();

        for (Statement s : statements) {
            result.add(getStatementResponse(s));
        }
        log.debug("Result list statements-{}", statements);
        return result;
    }

    public StatementResponseDto getStatementResponse(Statement statement) {
        log.info("Input statement-{}", statement);
        StatementResponseDto dto = StatementResponseDto.builder()
                .id(statement.getId())
                .status(statement.getStatus())
                .creationDate(statement.getCreationDate())
                .appliedOffer(statement.getAppliedOffer())
                .signDate(statement.getSignDate())
                .sesCode(statement.getSesCode())
                .statusHistory(statement.getStatusHistory())
                .build();

        if (statement.getClient() != null) {
            dto.setClient(clientResponse(statement.getClient()));
        }

        if (statement.getCredit() != null) {
            dto.setCredit(creditResponse(statement.getCredit()));
        }

        return dto;
    }

    private ClientResponseDto clientResponse(Client client) {
        log.info("Input client-{}", client);
        return ClientResponseDto.builder()
                .id(client.getId())
                .lastName(client.getLastName())
                .firstName(client.getFirstName())
                .middleName(client.getMiddleName())
                .birthday(client.getBirthday())
                .email(client.getEmail())
                .gender(client.getGender())
                .maritalStatus(client.getMaritalStatus())
                .dependentAmount(client.getDependentAmount())
                .accountNumber(client.getAccountNumber())
                .passport(client.getPassport())
                .employment(client.getEmployment())
                .build();
    }

    private CreditResponseDto creditResponse(Credit credit) {
        log.info("Input credit-{}", credit);
        return CreditResponseDto.builder()
                .id(credit.getId())
                .amount(credit.getAmount())
                .term(credit.getTerm())
                .monthlyPayment(credit.getMonthlyPayment())
                .rate(credit.getRate())
                .psk(credit.getPsk())
                .paymentSchedule(credit.getPaymentSchedule())
                .isInsuranceEnabled(credit.getIsInsuranceEnabled())
                .isSalaryClient(credit.getIsSalaryClient())
                .creditStatus(credit.getCreditStatus())
                .build();
    }
}
