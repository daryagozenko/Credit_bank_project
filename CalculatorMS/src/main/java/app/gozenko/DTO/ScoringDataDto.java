package app.gozenko.DTO;

import app.gozenko.Enums.Gender;
import app.gozenko.Enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDto {
    private BigDecimal amount;
    private Integer term;

    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthday;
    private Gender gender;

    private String passportSeries;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatus maritalStatus;

    private Integer dependentAmount;
    private EmploymentDto employment;
    private String accountNumber;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
}
