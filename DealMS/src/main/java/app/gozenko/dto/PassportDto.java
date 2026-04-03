package app.gozenko.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Паспортные данные")
public class PassportDto {

    @Schema(description = "Серия паспорта", example = "1212",
            minLength = 4, maxLength = 4)
    private String series;

    @Schema(description = "Номер паспорта", example = "121212",
            minLength = 6, maxLength = 6)
    private String number;

    @Schema(description = "Название подразделения", example = "МВД России")
    private String issueBranch;

    @Schema(description = "Название подразделения", example = "МВД России")
    private LocalDate issueDate;
}
