package app.gozenko.DTO;

import app.gozenko.Enums.EmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDto {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    //TODO: сделать enum Position
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
