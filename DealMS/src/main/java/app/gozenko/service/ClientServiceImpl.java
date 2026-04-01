package app.gozenko.service;

import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.PassportDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Statement;
import app.gozenko.exception.ClientExistsException;
import app.gozenko.repository.ClientRepository;
import app.gozenko.service.interfaces.ClientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client createClient(LoanStatementRequestDto request) {
        if(checkClientExist(request)){
            throw new ClientExistsException("Клиент с такими паспортными данными существует");
        }
        return clientRepository.save(fillClientInfo(request));
    }

    @Override
    public Client findById(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден клиент: " + clientId));
    }

    @Transactional
    @Override
    public void updateClient(Statement statement, FinishRegistrationRequestDto finishRegistration) {
        Client client = findById(statement.getClient().getId());
        clientRepository.save(updateClientInfo(client, finishRegistration));
    }

    private boolean checkClientExist(LoanStatementRequestDto request) {
        String passportNum = request.getPassportNumber();
        String passportSer = request.getPassportSeries();
        return clientRepository.existsByPassportSeries(passportSer) &&
                clientRepository.existsByPassportNumber(passportNum);
    }

    private Client updateClientInfo(Client client, FinishRegistrationRequestDto finishRegistration) {
        client.setGender(finishRegistration.getGender());
        client.setMaritalStatus(finishRegistration.getMaritalStatus());
        client.setDependentAmount(finishRegistration.getDependentAmount());
        client.getPassport().setIssueDate(finishRegistration.getPassportIssueDate());
        client.getPassport().setIssueBranch(finishRegistration.getPassportIssueBranch());
        client.setEmployment(finishRegistration.getEmployment());
        client.setAccountNumber(finishRegistration.getAccountNumber());

        return client;
    }

    private PassportDto fillPassportInfo(LoanStatementRequestDto request) {
        return PassportDto.builder()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber())
                .build();
    }

    private Client fillClientInfo(LoanStatementRequestDto request) {
        return Client.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .birthday(request.getBirthday())
                .email(request.getEmail())
                .passport(fillPassportInfo(request))
                .build();
    }
}
