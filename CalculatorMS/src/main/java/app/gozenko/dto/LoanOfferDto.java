package app.gozenko.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Предложение по кредиту")
public class LoanOfferDto {

    @Schema(description = "Идентификатор", example = "a1b2c3")
    private UUID statementId;

    @Schema(description = "Запрашиваемая сумма", example = "50000.00", format = "decimal")
    private BigDecimal requestedAmount;

    @Schema(description = "Итоговая сумма", example = "55000.00", format = "decimal")
    private BigDecimal totalAmount;

    @Schema(description = "Срок", example = "6")
    private Integer term;

    @Schema(description = "Ежемесячный платеж", example = "3000.00", format = "decimal")
    private BigDecimal monthlyPayment;

    @Schema(description = "Ставка по кредиту", example = "15.50", format = "decimal")
    private BigDecimal rate;

    @Schema(description = "Наличие страховки", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Зарплатный клиент", example = "true")
    private Boolean isSalaryClient;
}
