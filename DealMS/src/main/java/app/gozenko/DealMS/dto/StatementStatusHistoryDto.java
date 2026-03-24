package app.gozenko.DealMS.dto;
import app.gozenko.DealMS.enums.StatementStatus;
import app.gozenko.DealMS.enums.StatusChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementStatusHistoryDto {
    private StatementStatus status;
    private LocalDateTime time;
    private StatusChangeType changeType;
}
