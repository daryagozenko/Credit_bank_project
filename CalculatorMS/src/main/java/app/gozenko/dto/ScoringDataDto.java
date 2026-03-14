package app.gozenko.dto;

import app.gozenko.enums.Gender;
import app.gozenko.enums.MaritalStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDto {
    @Min(value = 20000, message = "Сумма должна быть больше 20000")
    private BigDecimal amount;
    @Min(value = 6, message = "Срок должен быть не меньше 6")
    private Integer term;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$",
            message = "Имя должно быть от 2 до 30 символов")
    private String firstName;
    @Pattern(regexp = "^[a-zA-Z]{2,30}$",
            message = "Фамилия должна быть от 2 до 30 символов")
    private String lastName;
    @Pattern(regexp = "^[a-zA-Z]{2,30}$",
            message = "Отчество должно быть от 2 до 30 символов")
    private String middleName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;
    @NotNull
    private Gender gender;

    @Pattern(regexp = "^\\d{4}$",
            message = "Серия паспорта должна быть 4 символа")
    private String passportSeries;
    @Pattern(regexp = "^\\d{6}$",
            message = "Номер паспорта должен быть 6 символов")
    private String passportNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Дата получения паспорта должна быть в прошлом")
    private LocalDate passportIssueDate;
    @NotNull
    private String passportIssueBranch;
    @NotNull
    private MaritalStatus maritalStatus;

    @NotNull
    private Integer dependentAmount;
    @NotNull
    private EmploymentDto employment;
    @NotNull
    private String accountNumber;
    @NotNull
    private Boolean isInsuranceEnabled;
    @NotNull
    private Boolean isSalaryClient;
}
