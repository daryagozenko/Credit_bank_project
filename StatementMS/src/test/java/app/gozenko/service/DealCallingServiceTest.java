package app.gozenko.service;

import app.gozenko.client.DealClient;
import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.exception.DealClientException;
import app.gozenko.exception.DealServerException;
import app.gozenko.exception.JsonException;
import app.gozenko.utils.StubGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DealCallingServiceTest {

    @Mock
    private DealClient dealClient;

    @InjectMocks
    private DealCallingService dealCallingService;

    private LoanStatementRequestDto loanStatement;
    private LoanOfferDto loanOffer;

    @BeforeEach
    void setUp() {
        loanStatement = StubGenerator.createEmptyLoanStatementRequest();
        loanOffer = StubGenerator.createEmptyLoanOfferDto();
    }

    @Test
    @DisplayName("Успешное получение списка предложений")
    void getListLoanOffers_Success() {
        List<LoanOfferDto> offers = List.of(loanOffer);
        when(dealClient.getLoanOffers(loanStatement)).thenReturn(offers);

        List<LoanOfferDto> result = dealCallingService.getLoanOffers(loanStatement);

        assertEquals(offers, result);

        verify(dealClient).getLoanOffers(loanStatement);
    }

    @Test
    @DisplayName("Получение списка предложений - ошибка 4хх")
    void getListLoanOffers_ClientException() {
        when(dealClient.getLoanOffers(loanStatement)).thenThrow(new DealClientException("Ошибка 4хх"));

        assertThrows(DealClientException.class, () ->
                dealCallingService.getLoanOffers(loanStatement));

        verify(dealClient).getLoanOffers(loanStatement);
    }

    @Test
    @DisplayName("Получение списка предложений - ошибка 5хх")
    void getListLoanOffers_SeverException() {
        when(dealClient.getLoanOffers(loanStatement)).thenThrow(new DealServerException("Ошибка 5хх"));

        assertThrows(DealServerException.class, () ->
                dealCallingService.getLoanOffers(loanStatement));

        verify(dealClient).getLoanOffers(loanStatement);

    }

    @Test
    @DisplayName("Получение списка предложений - ошибка парсинга JSON")
    void getListLoanOffers_JsonException() {
        when(dealClient.getLoanOffers(loanStatement)).thenThrow(new JsonException("Ошибка парсинга"));

        assertThrows(JsonException.class, () ->
                dealCallingService.getLoanOffers(loanStatement));

        verify(dealClient).getLoanOffers(loanStatement);

    }

    @Test
    @DisplayName("Успешный выбор предложения")
    void selectLoanOffer_Success() {
        dealCallingService.selectLoanOffer(loanOffer);

        verify(dealClient).selectOffer(loanOffer);
    }

    @Test
    @DisplayName("Выбор предложения - ошибка 4хх")
    void selectLOanOffer_ClientException() {
        doThrow(new DealClientException("Ошибка клиента 4xx"))
                .when(dealClient).selectOffer(loanOffer);

        assertThrows(DealClientException.class,
                () -> dealCallingService.selectLoanOffer(loanOffer));

        verify(dealClient).selectOffer(loanOffer);
    }

    @Test
    @DisplayName("Выбор предложения - ошибка 5хх")
    void selectLOanOffer_SeverException() {
        doThrow(new DealServerException("Ошибка сервера 5xx"))
                .when(dealClient).selectOffer(loanOffer);

        assertThrows(DealServerException.class,
                () -> dealCallingService.selectLoanOffer(loanOffer));

        verify(dealClient).selectOffer(loanOffer);
    }

    @Test
    @DisplayName("Выбор предложения - ошибка парсинга JSON")
    void selectLOanOffer_JsonException() {
        doThrow(new JsonException("Ошибка парсинга"))
                .when(dealClient).selectOffer(loanOffer);

        assertThrows(JsonException.class,
                () -> dealCallingService.selectLoanOffer(loanOffer));

        verify(dealClient).selectOffer(loanOffer);
    }
}
