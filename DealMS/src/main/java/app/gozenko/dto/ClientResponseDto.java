package app.gozenko.dto;

import app.gozenko.enums.Gender;
import app.gozenko.enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDto {
    private UUID id;

    private String lastName;

    private String firstName;

    private String middleName;

    private LocalDate birthday;

    private String email;

    private Gender gender;

    private MaritalStatus maritalStatus;

    private Integer dependentAmount;

    private String accountNumber;

    private PassportDto passport;

    private EmploymentDto employment;
}
