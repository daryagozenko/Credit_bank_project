package app.gozenko.dto;

import app.gozenko.enums.EmploymentStatus;
import app.gozenko.enums.Position;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDto {

    @NotNull
    @Schema(description = "Статус занятости", example = "WORK")
    private EmploymentStatus employmentStatus;

    @NotNull
    @Schema(description = "ИНН работодателя", example = "123456")
    private String employerINN;

    @NotNull
    @Min(value = 0, message = "Зарплата должна быть положительной")
    @Schema(description = "Зарплата", example = "80000")
    private BigDecimal salary;

    @NotNull
    @Schema(description = "Должность", example = "WORKER")
    private Position position;

    @NotNull
    @Min(value = 0, message = "Общий стаж должен быть положительным")
    @Schema(description = "Общий стаж работы в месяцах", example = "12")
    private Integer workExperienceTotal;

    @NotNull
    @Min(value = 0, message = "Текущий стаж должен быть положительным")
    @Schema(description = "Текущий стаж работы в месяцах", example = "6")
    private Integer workExperienceCurrent;
}