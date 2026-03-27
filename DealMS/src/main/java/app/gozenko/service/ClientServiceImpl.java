package app.gozenko.service;

import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.entity.Client;
import app.gozenko.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl {
    private final ClientRepository clientRepository;

    public Client createClient(LoanStatementRequestDto request) {
        Client client = Client.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .birthday(request.getBirthday())
                .email(request.getEmail())
                .build();
        //TODO: выброс исключения, если есть клиент с такими же паспортными данными
        return clientRepository.save(client);
    }
}
