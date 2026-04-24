package app.gozenko.dto;

import app.gozenko.enums.Gender;
import app.gozenko.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Schema(name = "Оформление итоговой заявки")
public class ScoringDataDto {

    @Min(value = 20000, message = "Сумма должна быть больше 20000")
    @Schema(description = "Сумма кредита", example = "1030000.12", format = "decimal")
    private BigDecimal amount;

    @Min(value = 6, message = "Срок должен быть не меньше 6")
    @Schema(description = "Срок кредита в месяцах", example = "12")
    private Integer term;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$",
            message = "Имя должно быть от 2 до 30 символов, символы латинские")
    @NotNull
    @Schema(description = "Имя", example = "dasha")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$",
            message = "Фамилия должна быть от 2 до 30 символов, символы латинские")
    @NotNull
    @Schema(description = "Фамилия", example = "gozenko")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$",
            message = "Отчество должно быть от 2 до 30 символов, символы латинские")
    @Schema(description = "Отчество", example = "sergeevna")
    private String middleName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Дата рождения должна быть в прошлом")
    @Schema(description = "Дата рождения", example = "2005-05-01")
    private LocalDate birthday;

    @NotNull(message = "необходимо заполнить gender")
    @Schema(description = "Пол", example = "FEMALE")
    private Gender gender;

    @Pattern(regexp = "^\\d{4}$",
            message = "Серия паспорта должна быть 4 символа")
    @Schema(description = "Серия паспорта", example = "1111")
    private String passportSeries;

    @Pattern(regexp = "^\\d{6}$",
            message = "Номер паспорта должен быть 6 символов")
    @Schema(description = "Номер паспорта", example = "121212")
    private String passportNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Дата получения паспорта должна быть в прошлом")
    @Schema(description = "Дата выдачи паспорта", example = "2025-07-01")
    private LocalDate passportIssueDate;

    @NotNull(message = "необходимо заполнить passportIssueBranch")
    @Schema(description = "Кем выдан паспорт", example = "МВД России")
    private String passportIssueBranch;

    @NotNull(message = "необходимо заполнить maritalStatus")
    @Schema(description = "Семейное положение", example = "NOT_MARRIED")
    private MaritalStatus maritalStatus;

    @NotNull(message = "необходимо заполнить dependentAmount")
    @Schema(description = "Количество иждивенцев", example = "0")
    private Integer dependentAmount;

    @NotNull(message = "необходимо заполнить employment")
    private EmploymentDto employment;

    @NotNull(message = "необходимо заполнить accountNumber")
    @Schema(description = "Номер счета", example = "2000-563-78")
    private String accountNumber;

    @NotNull(message = "необходимо заполнить isInsuranceEnabled")
    @Schema(description = "Наличие страховки", example = "true")
    private Boolean isInsuranceEnabled;

    @NotNull(message = "необходимо заполнить isSalaryClient")
    @Schema(description = "Является зарплатным клиентом", example = "true")
    private Boolean isSalaryClient;
}