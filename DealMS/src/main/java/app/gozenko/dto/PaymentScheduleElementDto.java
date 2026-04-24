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
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "График платежей")
public class PaymentScheduleElementDto {

    @Schema(description = "Номер платежа", example = "1")
    private Integer number;

    @Schema(description = "Дата платежа", example = "2026-01-09")
    private LocalDate date;

    @Schema(description = "Общая сумма платежа", example = "1234000.09", format = "decimal")
    private BigDecimal totalPayment;

    @Schema(description = "Сумма платежа по процентам", example = "234000.09", format = "decimal")
    private BigDecimal interestPayment;

    @Schema(description = "Сумма платежа по основному долгу", example = "810000.09", format = "decimal")
    private BigDecimal debtPayment;

    @Schema(description = "Остаток суммы по основному долгу", example = "23000.07", format = "decimal")
    private BigDecimal remainingDebt;
}
