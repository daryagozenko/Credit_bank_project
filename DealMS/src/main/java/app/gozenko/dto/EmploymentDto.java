package app.gozenko.dto;

import app.gozenko.enums.EmploymentStatus;
import app.gozenko.enums.Position;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Информация о занятости")
public class EmploymentDto {

    @NotNull(message = "необходимо заполнить employmentStatus")
    @Schema(description = "Статус занятости", example = "EMPLOYED")
    private EmploymentStatus employmentStatus;

    @NotBlank(message = "ИНН работодателя не может быть пустым")
    @Pattern(regexp = "^\\d{10}$|^\\d{12}$",
            message = "ИНН должен содержать 10 или 12 цифр")
    @Schema(description = "ИНН работодателя", example = "770123456789")
    private String employerINN;

    @NotNull(message = "необходимо заполнить salary")
    @Schema(description = "Размер заработной платы", example = "75000.00")
    private BigDecimal salary;

    @NotNull(message = "необходимо заполнить position")
    @Schema(description = "Должность", example = "TOP_MANAGER")
    private Position position;

    @NotNull(message = "необходимо заполнить workExperienceTotal")
    @Min(value = 12, message = "Общий стаж должен быть не менее 12 месяцев")
    @Schema(description = "Общий стаж работы в месяцах", example = "48")
    private Integer workExperienceTotal;

    @NotNull(message = "необходимо заполнить workExperienceCurrent")
    @Min(value = 3, message = "Стаж на текущем месте должен быть не менее 3 месяцев")
    @Schema(description = "Стаж работы на текущем месте в месяцах", example = "24")
    private Integer workExperienceCurrent;
}