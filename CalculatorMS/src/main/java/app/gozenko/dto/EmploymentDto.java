package app.gozenko.dto;

import app.gozenko.enums.EmploymentStatus;
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
