package app.gozenko.DealMS.service;

import app.gozenko.DealMS.utils.StubGenerator;
import app.gozenko.dto.EmploymentDto;
import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.PassportDto;
import app.gozenko.entity.Client;
import app.gozenko.entity.Statement;
import app.gozenko.enums.EmploymentStatus;
import app.gozenko.enums.Gender;
import app.gozenko.enums.MaritalStatus;
import app.gozenko.enums.Position;
import app.gozenko.exception.ClientExistsException;
import app.gozenko.repository.ClientRepository;
import app.gozenko.service.ClientServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private LoanStatementRequestDto validLoanRequest;
    private FinishRegistrationRequestDto validFinishRequest;
    private Client savedClient;
    private Statement statement;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        validLoanRequest = StubGenerator.createValidLoanStatementRequest();

        validFinishRequest = StubGenerator.createValidFinishRegistrationRequest();

        savedClient = StubGenerator.createClient(clientId);

        statement = StubGenerator.createStatementWithClient(savedClient);
    }

    @Test
    @DisplayName("Успешное создание клиента из заявки на кредит - клиент с данной серией паспорта не существует")
    void createClientWithSeries_Success() {
        when(clientRepository.existsByPassportSeries(validLoanRequest.getPassportSeries()))
                .thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        Client result = clientService.createClient(validLoanRequest);

        assertNotNull(result);
        assertEquals(savedClient.getId(), result.getId());

        verify(clientRepository).existsByPassportSeries(validLoanRequest.getPassportSeries());
        verify(clientRepository, never()).existsByPassportNumber(anyString());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("Успешное создание клиента из заявки на кредит - клиент с данным номером паспорта не существует")
    void createClientWithNumber_Success() {
        when(clientRepository.existsByPassportSeries(validLoanRequest.getPassportSeries()))
                .thenReturn(true);
        when(clientRepository.existsByPassportNumber(validLoanRequest.getPassportNumber()))
                .thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        Client result = clientService.createClient(validLoanRequest);

        assertNotNull(result);
        assertEquals(savedClient.getId(), result.getId());

        verify(clientRepository).existsByPassportSeries(validLoanRequest.getPassportSeries());
        verify(clientRepository).existsByPassportNumber(validLoanRequest.getPassportNumber());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("Создание клиента - клиент уже существует, выбрасывается исключение")
    void createClient_ClientAlreadyExists_ThrowsClientExistsException() {
        when(clientRepository.existsByPassportSeries(validLoanRequest.getPassportSeries()))
                .thenReturn(true);
        when(clientRepository.existsByPassportNumber(validLoanRequest.getPassportNumber()))
                .thenReturn(true);

        ClientExistsException exception = assertThrows(
                ClientExistsException.class,
                () -> clientService.createClient(validLoanRequest)
        );

        assertTrue(exception.getMessage().contains("Клиент с такими паспортными данными существует"));

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Создание клиента - все поля корректно маппятся")
    void createClient_AllFieldsMappedCorrectly() {
        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        when(clientRepository.save(clientCaptor.capture())).thenReturn(savedClient);

        clientService.createClient(validLoanRequest);

        Client capturedClient = clientCaptor.getValue();

        assertEquals(validLoanRequest.getLastName(), capturedClient.getLastName());
        assertEquals(validLoanRequest.getFirstName(), capturedClient.getFirstName());
        assertEquals(validLoanRequest.getMiddleName(), capturedClient.getMiddleName());
        assertEquals(validLoanRequest.getBirthday(), capturedClient.getBirthday());
        assertEquals(validLoanRequest.getEmail(), capturedClient.getEmail());
        assertEquals(validLoanRequest.getPassportSeries(), capturedClient.getPassport().getSeries());
        assertEquals(validLoanRequest.getPassportNumber(), capturedClient.getPassport().getNumber());
    }

    @Test
    @DisplayName("Поиск клиента по ID - успешно")
    void findById_Success() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(savedClient));

        Client result = clientService.findById(clientId);

        assertNotNull(result);
        assertEquals(savedClient.getId(), result.getId());
        verify(clientRepository).findById(clientId);
    }

    @Test
    @DisplayName("Поиск клиента по ID - клиент не найден, выбрасывается исключение")
    void findById_ClientNotFound_ThrowsEntityNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(clientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> clientService.findById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Не найден клиент"));
        verify(clientRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Обновление клиента - успешное обновление всех полей")
    void updateClient_Success() {
        when(clientRepository.findById(savedClient.getId())).thenReturn(Optional.of(savedClient));
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        clientService.updateClient(statement, validFinishRequest);

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(clientCaptor.capture());

        Client updatedClient = clientCaptor.getValue();

        assertEquals(validFinishRequest.getGender(), updatedClient.getGender());
        assertEquals(validFinishRequest.getMaritalStatus(), updatedClient.getMaritalStatus());
        assertEquals(validFinishRequest.getDependentAmount(), updatedClient.getDependentAmount());
        assertEquals(validFinishRequest.getPassportIssueDate(), updatedClient.getPassport().getIssueDate());
        assertEquals(validFinishRequest.getPassportIssueBranch(), updatedClient.getPassport().getIssueBranch());
        assertEquals(validFinishRequest.getEmployment(), updatedClient.getEmployment());
        assertEquals(validFinishRequest.getAccountNumber(), updatedClient.getAccountNumber());
    }

    @Test
    @DisplayName("Обновление клиента - клиент не найден, выбрасывается исключение")
    void updateClient_ClientNotFound_ThrowsEntityNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        Client nonExistentClient = StubGenerator.createClient(nonExistentId);
        Statement statementWithNonExistentClient = StubGenerator.createStatementWithClient(nonExistentClient);

        when(clientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> clientService.updateClient(statementWithNonExistentClient, validFinishRequest)
        );

        assertTrue(exception.getMessage().contains("Не найден клиент"));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @ParameterizedTest
    @MethodSource("provideClientDataScenarios")
    @DisplayName("Создание клиента с различными данными")
    void createClient_WithDifferentData_Success(
            String lastName,
            String firstName,
            String middleName,
            LocalDate birthday,
            String email,
            String passportSeries,
            String passportNumber) {

        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .lastName(lastName)
                .firstName(firstName)
                .middleName(middleName)
                .birthday(birthday)
                .email(email)
                .passportSeries(passportSeries)
                .passportNumber(passportNumber)
                .build();

        Client expectedClient = Client.builder()
                .id(UUID.randomUUID())
                .lastName(lastName)
                .firstName(firstName)
                .middleName(middleName)
                .birthday(birthday)
                .email(email)
                .passport(PassportDto.builder()
                        .series(passportSeries)
                        .number(passportNumber)
                        .build())
                .build();

        when(clientRepository.save(any(Client.class))).thenReturn(expectedClient);

        Client result = clientService.createClient(request);

        assertNotNull(result);
        assertEquals(lastName, result.getLastName());
        assertEquals(firstName, result.getFirstName());
        assertEquals(middleName, result.getMiddleName());
        assertEquals(birthday, result.getBirthday());
        assertEquals(email, result.getEmail());
        assertEquals(passportSeries, result.getPassport().getSeries());
        assertEquals(passportNumber, result.getPassport().getNumber());
    }

    private static Stream<Arguments> provideClientDataScenarios() {
        return Stream.of(
                Arguments.of(
                        "Иванов", "Иван", "Иванович",
                        LocalDate.of(1990, 1, 15),
                        "ivan@example.com",
                        "1234", "567890"
                ),
                Arguments.of(
                        "Smith", "John", null,
                        LocalDate.of(1985, 5, 20),
                        "john@example.com",
                        "9876", "543210"
                ),
                Arguments.of(
                        "Петрова", "Анна", "Сергеевна",
                        LocalDate.of(1995, 12, 10),
                        "anna@example.com",
                        "5555", "123456"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideEmploymentUpdateScenarios")
    @DisplayName("Обновление клиента с различными данными о занятости")
    void updateClient_WithDifferentEmployment_Success(
            EmploymentDto employment,
            Gender gender,
            MaritalStatus maritalStatus,
            int dependentAmount) {

        FinishRegistrationRequestDto finishRequest = FinishRegistrationRequestDto.builder()
                .gender(gender)
                .maritalStatus(maritalStatus)
                .dependentAmount(dependentAmount)
                .employment(employment)
                .passportIssueDate(LocalDate.of(2020, 1, 1))
                .passportIssueBranch("Branch")
                .accountNumber("1234567890")
                .build();

        when(clientRepository.findById(savedClient.getId())).thenReturn(Optional.of(savedClient));
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        clientService.updateClient(statement, finishRequest);

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(clientCaptor.capture());

        Client updatedClient = clientCaptor.getValue();

        assertEquals(employment, updatedClient.getEmployment());
        assertEquals(gender, updatedClient.getGender());
        assertEquals(maritalStatus, updatedClient.getMaritalStatus());
        assertEquals(dependentAmount, updatedClient.getDependentAmount());
    }

    private static Stream<Arguments> provideEmploymentUpdateScenarios() {
        EmploymentDto workerEmployment = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerINN("1234567890")
                .salary(new BigDecimal("100000"))
                .position(Position.WORKER)
                .workExperienceTotal(60)
                .workExperienceCurrent(24)
                .build();

        EmploymentDto managerEmployment = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerINN("0987654321")
                .salary(new BigDecimal("200000"))
                .position(Position.MANAGER)
                .workExperienceTotal(120)
                .workExperienceCurrent(48)
                .build();

        return Stream.of(
                Arguments.of(workerEmployment, Gender.MALE, MaritalStatus.MARRIED, 1),
                Arguments.of(managerEmployment, Gender.FEMALE, MaritalStatus.DIVORCED, 0)
        );
    }
}