package app.gozenko.dto;

import app.gozenko.enums.StatementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementResponseDto {
    private UUID id;

    private StatementStatus status;

    private LocalDateTime creationDate;

    private LoanOfferDto appliedOffer;

    private LocalDateTime signDate;

    private Integer sesCode;

    private List<StatementStatusHistoryDto> statusHistory;

    private ClientResponseDto client;

    private CreditResponseDto credit;
}
