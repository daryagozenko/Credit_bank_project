package app.gozenko.dto;

import app.gozenko.enums.CreditStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditResponseDto {
    private UUID id;

    private BigDecimal amount;

    private Integer term;

    private BigDecimal monthlyPayment;

    private BigDecimal rate;

    private BigDecimal psk;

    private List<PaymentScheduleElementDto> paymentSchedule;

    private Boolean isInsuranceEnabled;

    private Boolean isSalaryClient;

    private CreditStatus creditStatus;
}
