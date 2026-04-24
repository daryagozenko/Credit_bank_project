package app.gozenko.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Оформление кредитной заявки")
public class LoanStatementRequestDto {

    @Schema(description = "Сумма кредита", example = "1000000.00", format = "decimal")
    private BigDecimal amount;

    @Schema(description = "Срок кредита в месяцах", example = "12")
    private Integer term;

    @Schema(description = "Имя", example = "dasha")
    private String firstName;

    @Schema(description = "Фамилия", example = "gozenko")
    private String lastName;

    @Schema(description = "Отчество", example = "sergeevna")
    private String middleName;

    @Schema(description = "Email", example = "email@bk.ru")
    private String email;

    @Schema(description = "Дата рождения", example = "2005-05-01")
    private LocalDate birthday;

    @Schema(description = "Серия паспорта", example = "1111")
    private String passportSeries;

    @Schema(description = "Номер паспорта", example = "121212")
    private String passportNumber;
}
