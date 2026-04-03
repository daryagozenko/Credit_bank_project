package app.gozenko.dto;
import app.gozenko.enums.StatementStatus;
import app.gozenko.enums.StatusChangeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "История статусов заявки")
public class StatementStatusHistoryDto {

    @Schema(description = "Статус заявки", example = "APPROVED")
    private StatementStatus status;

    @Schema(description = "Время установки статуса", example = "2026-02-02")
    private LocalDateTime time;

    @Schema(description = "Способ установки статуса", example = "AUTOMATIC")
    private StatusChangeType changeType;
}
