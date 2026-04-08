package app.gozenko.calculator.service;

import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.exception.ValidationDataException;
import app.gozenko.service.PreScoringServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.stream.Stream;

import static app.gozenko.calculator.utils.StubGenerator.createLoanStatementRequestWithAge;
import static app.gozenko.calculator.utils.StubGenerator.createLoanStatementRequestWithBirthday;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PreScoringServiceImplTest {

    @InjectMocks
    private PreScoringServiceImpl preScoringService;

    private Integer legalAge;

    @BeforeEach
    void setUp() {
        legalAge = 18;
        ReflectionTestUtils.setField(preScoringService, "legalAge", legalAge);
    }

    //TODO: проверка даты на неправильный формат

    @Test
    @DisplayName("Успешный прескоринг LoanStatementRequestDto для совершеннолетнего клиента")
    void preScoringLoan_Success() {
        LoanStatementRequestDto request = createLoanStatementRequestWithAge(30);

        assertDoesNotThrow(() -> preScoringService.preScoringLoan(request));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAgesForLoanStatement")
    @DisplayName("Проверка исключений для LoanStatementRequestDto при несовершеннолетнем возрасте")
    void preScoringLoan_Underage_ThrowsException(int age, String expectedMessage) {
        LoanStatementRequestDto request = createLoanStatementRequestWithAge(age);

        ValidationDataException exception = assertThrows(
                ValidationDataException.class,
                () -> preScoringService.preScoringLoan(request)
        );
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideValidAgeBoundaries")
    @DisplayName("Проверка граничных значений возраста")
    void preScoringLoan_AgeBoundaries_Success(int age) {
        LoanStatementRequestDto request = createLoanStatementRequestWithAge(age);

        assertDoesNotThrow(() -> preScoringService.preScoringLoan(request));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAgeBoundaries")
    @DisplayName("Проверка граничных значений возраста (должно выбрасывать исключение)")
    void preScoringLoan_AgeBoundaries_ThrowsException(int age) {
        LoanStatementRequestDto request = createLoanStatementRequestWithAge(age);

        assertThrows(
                ValidationDataException.class,
                () -> preScoringService.preScoringLoan(request)
        );
    }

    @Test
    @DisplayName("Проверка с датой рождения ровно legalAge лет назад")
    void preScoringLoan_ExactlyLegalAge_Success() {
        LocalDate birthday = LocalDate.now().minusYears(legalAge);
        LoanStatementRequestDto request = createLoanStatementRequestWithBirthday(birthday);

        assertDoesNotThrow(() -> preScoringService.preScoringLoan(request));
    }

    @Test
    @DisplayName("Проверка с датой рождения на день меньше legalAge")
    void preScoringLoan_OneDayLessThanLegalAge_ThrowsException() {
        LocalDate birthday = LocalDate.now().minusYears(legalAge).plusDays(1);
        LoanStatementRequestDto request = createLoanStatementRequestWithBirthday(birthday);

        ValidationDataException exception = assertThrows(
                ValidationDataException.class,
                () -> preScoringService.preScoringLoan(request)
        );

        assertTrue(exception.getMessage().contains(String.valueOf(legalAge)));
    }

    @ParameterizedTest
    @MethodSource("provideLegalAgeScenarios")
    @DisplayName("Проверка валидации возраста с разными значениями legalAge")
    void preScoringLoan_DifferentLegalAges(int legalAgeValue, int yearsToSubtract, boolean shouldThrow) {
        ReflectionTestUtils.setField(preScoringService, "legalAge", legalAgeValue);

        LocalDate birthday = LocalDate.now().minusYears(yearsToSubtract);
        LoanStatementRequestDto request = createLoanStatementRequestWithBirthday(birthday);

        if (shouldThrow) {
            ValidationDataException exception = assertThrows(
                    ValidationDataException.class,
                    () -> preScoringService.preScoringLoan(request)
            );
            assertTrue(exception.getMessage().contains(String.valueOf(legalAgeValue)));
        } else {
            assertDoesNotThrow(() -> preScoringService.preScoringLoan(request));
        }
    }

    @Test
    @DisplayName("Проверка валидации через аннотации @Valid")
    void preScoringLoan_ValidationAnnotations() {
        LoanStatementRequestDto invalidRequest = LoanStatementRequestDto.builder()
                .amount(null)
                .term(null)
                .firstName("")
                .lastName("")
                .birthday(null)
                .email("invalid-email")
                .passportSeries("")
                .passportNumber("")
                .build();

        assertThrows(Exception.class, () -> preScoringService.preScoringLoan(invalidRequest));
    }

    private static Stream<Arguments> provideLegalAgeScenarios() {
        return Stream.of(
                Arguments.of(18, 18, false),
                Arguments.of(18, 17, true),
                Arguments.of(18, 19, false),
                Arguments.of(25, 25, false),
                Arguments.of(16, 16, false)
        );
    }

    private static Stream<Arguments> provideInvalidAgesForLoanStatement() {
        return Stream.of(
                Arguments.of(17, "Возраст должен быть больше 18"),
                Arguments.of(10, "Возраст должен быть больше 18"),
                Arguments.of(0, "Возраст должен быть больше 18"),
                Arguments.of(-5, "Возраст должен быть больше 18")
        );
    }

    private static Stream<Arguments> provideInvalidAgesForScoringData() {
        return Stream.of(
                Arguments.of(17, "Возраст должен быть больше 18"),
                Arguments.of(15, "Возраст должен быть больше 18"),
                Arguments.of(12, "Возраст должен быть больше 18"),
                Arguments.of(5, "Возраст должен быть больше 18")
        );
    }

    private static Stream<Arguments> provideValidAgeBoundaries() {
        return Stream.of(
                Arguments.of(18),
                Arguments.of(19),
                Arguments.of(25),
                Arguments.of(30),
                Arguments.of(45),
                Arguments.of(60),
                Arguments.of(65),
                Arguments.of(70),
                Arguments.of(80)
        );
    }

    private static Stream<Arguments> provideInvalidAgeBoundaries() {
        return Stream.of(
                Arguments.of(17),
                Arguments.of(16),
                Arguments.of(15),
                Arguments.of(10),
                Arguments.of(5),
                Arguments.of(0),
                Arguments.of(-1)
        );
    }
}