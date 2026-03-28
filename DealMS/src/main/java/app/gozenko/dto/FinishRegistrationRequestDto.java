package app.gozenko.dto;

import app.gozenko.enums.Gender;
import app.gozenko.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Завершение регистрации кредитной заявки")
public class FinishRegistrationRequestDto {

    @NotNull(message = "необходимо заполнить gender")
    @Schema(description = "Пол", example = "FEMALE")
    private Gender gender;

    @NotNull(message = "необходимо заполнить maritalStatus")
    @Schema(description = "Семейное положение", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Min(value = 0, message = "Количество иждивенцев не может быть отрицательным")
    @Max(value = 20, message = "Количество иждивенцев не может превышать 20")
    @Schema(description = "Количество иждивенцев", example = "2")
    private Integer dependentAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Дата выдачи паспорта должна быть в прошлом")
    @NotNull(message = "необходимо заполнить passportIssueDate")
    @Schema(description = "Дата выдачи паспорта", example = "2015-06-15")
    private LocalDate passportIssueDate;

    @Size(min = 5, max = 200, message = "Место выдачи паспорта должно быть от 5 до 200 символов")
    @Schema(description = "Кем выдан паспорт", example = "ОУФМС РОССИИ ПО ГОРОДУ МОСКВЕ")
    private String passportIssueBranch;

    @NotNull(message = "необходимо заполнить employment")
    @Schema(description = "Информация о трудоустройстве")
    private EmploymentDto employment;

    @Pattern(regexp = "^[0-9]{20}$", message = "Номер счета должен состоять из 20 цифр")
    @Schema(description = "Номер счета", example = "40817810099910004312")
    private String accountNumber;
}