package app.gozenko.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Оформление кредитной заявки")
public class LoanStatementRequestDto {

    @Schema(description = "Сумма кредита", example = "1000000")
    @Min(value = 20000, message = "Сумма должна быть больше 20000")
    private BigDecimal amount;

    @Schema(description = "Срок кредита в месяцах", example = "12")
    @Min(value = 6, message = "Срок должен быть не меньше 6")
    private Integer term;

    @Schema(description = "Имя", example = "dasha")
    @Pattern(regexp = "^[a-zA-Z]{2,30}$",
            message = "Имя должно быть от 2 до 30 символов")
    private String firstName;

    @Schema(description = "Фамилия", example = "gozenko")
    @Pattern(regexp = "^[a-zA-Z]{2,30}$",
            message = "Отчество должно быть от 2 до 30 символов")
    private String lastName;

    @Schema(description = "Отчество", example = "sergeevna")
    @Pattern(regexp = "^[a-zA-Z]{2,30}$",
            message = "Фамилия должна быть от 2 до 30 символов")
    private String middleName;

    @Schema(description = "Email", example = "email@bk.ru")
    @Pattern(regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$",
            message = "Некорреткный формат почты")
    private String email;

    @Schema(description = "Дата рождения", example = "2005-05-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;

    @Schema(description = "Серия паспорта", example = "1111")
    @Pattern(regexp = "^\\d{4}$",
            message = "Серия паспорта должна быть 4 символа")
    private String passportSeries;

    @Schema(description = "Номер паспорта", example = "121212")
    @Pattern(regexp = "^\\d{6}$",
            message = "Номер паспорта должен быть 6 символов")
    private String passportNumber;
}
