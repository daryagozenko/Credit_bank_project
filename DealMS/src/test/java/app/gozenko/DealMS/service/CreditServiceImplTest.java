package app.gozenko.DealMS.service;

import app.gozenko.DealMS.utils.StubGenerator;
import app.gozenko.dto.CreditDto;
import app.gozenko.entity.Credit;
import app.gozenko.enums.CreditStatus;
import app.gozenko.repository.CreditRepository;
import app.gozenko.service.CreditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditServiceImplTest {

    @Mock
    private CreditRepository creditRepository;

    @InjectMocks
    private CreditServiceImpl creditService;

    private CreditDto creditDto;
    private Credit savedCredit;

    @BeforeEach
    void setUp() {
        creditDto = StubGenerator.createExpectedCreditDto();
        savedCredit = StubGenerator.createCredit();
    }

    @Test
    @DisplayName("Успешное создание кредита из CreditDto")
    void createCredit_Success() {
        when(creditRepository.save(any(Credit.class))).thenReturn(savedCredit);

        Credit result = creditService.createCredit(creditDto);

        assertAll("Проверка созданного кредита",
                () -> assertNotNull(result),
                () -> assertEquals(savedCredit.getId(), result.getId()),
                () -> assertEquals(creditDto.getAmount(), result.getAmount()),
                () -> assertEquals(creditDto.getTerm(), result.getTerm()),
                () -> assertEquals(creditDto.getMonthlyPayment(), result.getMonthlyPayment()),
                () -> assertEquals(creditDto.getRate(), result.getRate()),
                () -> assertEquals(creditDto.getPsk(), result.getPsk()),
                () -> assertEquals(creditDto.getPaymentSchedule(), result.getPaymentSchedule()),
                () -> assertEquals(creditDto.getIsInsuranceEnabled(), result.getIsInsuranceEnabled()),
                () -> assertEquals(creditDto.getIsSalaryClient(), result.getIsSalaryClient()),
                () -> assertEquals(CreditStatus.CALCULATED, result.getCreditStatus())
        );

        verify(creditRepository).save(any(Credit.class));
    }

    @Test
    @DisplayName("Создание кредита - проверка маппинга всех полей")
    void createCredit_AllFieldsMappedCorrectly() {
        ArgumentCaptor<Credit> creditCaptor = ArgumentCaptor.forClass(Credit.class);
        when(creditRepository.save(creditCaptor.capture())).thenReturn(savedCredit);

        creditService.createCredit(creditDto);

        Credit capturedCredit = creditCaptor.getValue();

        assertAll("Проверка маппинга полей",
                () -> assertEquals(creditDto.getAmount(), capturedCredit.getAmount()),
                () -> assertEquals(creditDto.getTerm(), capturedCredit.getTerm()),
                () -> assertEquals(creditDto.getMonthlyPayment(), capturedCredit.getMonthlyPayment()),
                () -> assertEquals(creditDto.getRate(), capturedCredit.getRate()),
                () -> assertEquals(creditDto.getPsk(), capturedCredit.getPsk()),
                () -> assertEquals(creditDto.getPaymentSchedule(), capturedCredit.getPaymentSchedule()),
                () -> assertEquals(creditDto.getIsInsuranceEnabled(), capturedCredit.getIsInsuranceEnabled()),
                () -> assertEquals(creditDto.getIsSalaryClient(), capturedCredit.getIsSalaryClient()),
                () -> assertEquals(CreditStatus.CALCULATED, capturedCredit.getCreditStatus())
        );
    }

    @Test
    @DisplayName("Создание кредита - проверка сохранения в репозитории")
    void createCredit_RepositorySaveCalled() {
        when(creditRepository.save(any(Credit.class))).thenReturn(savedCredit);

        creditService.createCredit(creditDto);

        verify(creditRepository, times(1)).save(any(Credit.class));
    }


    @Test
    @DisplayName("Создание кредита - проверка с разными значениями")
    void createCredit_WithDifferentValues_Success() {
        CreditDto customCreditDto = StubGenerator.createExpectedCreditDto();

        Credit customSavedCredit = StubGenerator.createCredit();

        when(creditRepository.save(any(Credit.class))).thenReturn(customSavedCredit);

        Credit result = creditService.createCredit(customCreditDto);

        assertAll("Проверка кастомного кредита",
                () -> assertNotNull(result),
                () -> assertEquals(customCreditDto.getAmount(), result.getAmount()),
                () -> assertEquals(customCreditDto.getTerm(), result.getTerm()),
                () -> assertEquals(customCreditDto.getMonthlyPayment(), result.getMonthlyPayment()),
                () -> assertEquals(customCreditDto.getRate(), result.getRate()),
                () -> assertEquals(customCreditDto.getPsk(), result.getPsk()),
                () -> assertFalse(result.getIsInsuranceEnabled()),
                () -> assertFalse(result.getIsSalaryClient())
        );
    }
}