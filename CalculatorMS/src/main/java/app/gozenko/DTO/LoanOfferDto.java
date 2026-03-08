package app.gozenko.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanOfferDto {
    private UUID statementId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    public static LoanOfferDto createWithNewId(BigDecimal requestedAmount, BigDecimal totalAmount,
                                               Integer term, BigDecimal monthlyPayment,
                                               BigDecimal rate, Boolean isInsuranceEnabled,
                                               Boolean isSalaryClient) {
        LoanOfferDto dto = new LoanOfferDto();
        dto.setStatementId(UUID.randomUUID());
        dto.setRequestedAmount(requestedAmount);
        dto.setTotalAmount(totalAmount);
        dto.setTerm(term);
        dto.setMonthlyPayment(monthlyPayment);
        dto.setRate(rate);
        dto.setIsInsuranceEnabled(isInsuranceEnabled);
        dto.setIsSalaryClient(isSalaryClient);
        return dto;
    }
}
