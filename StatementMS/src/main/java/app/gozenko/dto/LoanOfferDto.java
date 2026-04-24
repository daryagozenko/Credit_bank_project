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
@Schema(name = "Кредитное предложение")
public class LoanOfferDto {

    @Schema(description = "Идентификатор заявки", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID statementId;

    @Schema(description = "Запрошенная сумма кредита", example = "1000000", format = "decimal")
    private BigDecimal requestedAmount;

    @Schema(description = "Общая сумма кредита с учетом страховки и зарплатного клиента", example = "1050000.68",
            format = "decimal")
    private BigDecimal totalAmount;

    @Schema(description = "Срок кредита в месяцах", example = "12")
    private Integer term;

    @Schema(description = "Ежемесячный платеж", example = "87500.00", format = "decimal")
    private BigDecimal monthlyPayment;

    @Schema(description = "Процентная ставка", example = "15.5", format = "decimal")
    private BigDecimal rate;

    @Schema(description = "Включена ли страховка", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Является ли клиент зарплатным", example = "false")
    private Boolean isSalaryClient;
}