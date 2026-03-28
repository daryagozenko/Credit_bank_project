package app.gozenko.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Кредитное предложение")
public class LoanOfferDto {

    @Schema(description = "Идентификатор заявки", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Идентификатор заявки не может быть null")
    private UUID statementId;

    @Schema(description = "Запрошенная сумма кредита", example = "1000000")
    @NotNull(message = "Запрошенная сумма не может быть null")
    @Min(value = 20000, message = "Сумма должна быть больше 20000")
    private BigDecimal requestedAmount;

    @Schema(description = "Общая сумма кредита с учетом страховки и зарплатного клиента", example = "1050000")
    @NotNull(message = "Общая сумма не может быть null")
    @Min(value = 20000, message = "Сумма должна быть больше 20000")
    private BigDecimal totalAmount;

    @Schema(description = "Срок кредита в месяцах", example = "12")
    @NotNull(message = "Срок не может быть null")
    @Min(value = 6, message = "Срок должен быть не меньше 6")
    private Integer term;

    @Schema(description = "Ежемесячный платеж", example = "87500.00")
    @NotNull(message = "Ежемесячный платеж не может быть null")
    @Positive(message = "Ежемесячный платеж должен быть положительным")
    private BigDecimal monthlyPayment;

    @Schema(description = "Процентная ставка", example = "15.5")
    @NotNull(message = "Ставка не может быть null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Ставка должна быть больше 0")
    private BigDecimal rate;

    @Schema(description = "Включена ли страховка", example = "true")
    @NotNull(message = "Статус страховки не может быть null")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Является ли клиент зарплатным", example = "false")
    @NotNull(message = "Статус зарплатного клиента не может быть null")
    private Boolean isSalaryClient;
}