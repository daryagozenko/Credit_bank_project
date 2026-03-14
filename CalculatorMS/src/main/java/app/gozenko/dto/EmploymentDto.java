package app.gozenko.dto;

import app.gozenko.enums.EmploymentStatus;
import app.gozenko.enums.Position;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDto {
    @NotNull
    private EmploymentStatus employmentStatus;
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String employerINN;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private BigDecimal salary;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Position position;
    @NotNull
    private Integer workExperienceTotal;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer workExperienceCurrent;
}
