package app.gozenko.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Кредитное предложение")
public class CreditDto {
    @Schema(description = "Сумма", example = "1000000.83", format = "decimal")
    private BigDecimal amount;
    @Schema(description = "Срок", example = "6")
    private Integer term;
    @Schema(description = "Ежемесячный платеж", example = "93000.23", format = "decimal")
    private BigDecimal monthlyPayment;
    @Schema(description = "Ставка по кредиту", example = "15", format = "decimal")
    private BigDecimal rate;
    @Schema(description = "Полная стоимость кредита", example = "17", format = "decimal")
    private BigDecimal psk;
    @Schema(description = "Наличие страховки", example = "true")
    private Boolean isInsuranceEnabled;
    @Schema(description = "Зарплатный клиент", example = "true")
    private Boolean isSalaryClient;
    @Schema(description = "График платежей")
    private List<PaymentScheduleElementDto> paymentSchedule;
}
