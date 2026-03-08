package app.gozenko.DTO;
import app.gozenko.Enums.StatementStatus;
import app.gozenko.Enums.StatusChangeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatementStatusHistoryDto {
    private StatementStatus status;
    private LocalDateTime time;
    private StatusChangeType changeType;
}
