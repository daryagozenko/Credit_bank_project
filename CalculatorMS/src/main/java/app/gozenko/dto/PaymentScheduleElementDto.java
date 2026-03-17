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
    @Schema(description = "Дата платежа", example = "2026-03-19")
    private LocalDate date;
    @Schema(description = "Текущий платеж", example = "12000")
    private BigDecimal totalPayment;
    @Schema(description = "Выплата процентов", example = "3000")
    private BigDecimal interestPayment;
    @Schema(description = "Выплата долга", example = "9000")
    private BigDecimal debtPayment;
    @Schema(description = "Остаток долга", example = "60000")
    private BigDecimal remainingDebt;
}
