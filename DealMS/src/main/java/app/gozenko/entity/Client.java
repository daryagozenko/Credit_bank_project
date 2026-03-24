package app.gozenko.entity;

import app.gozenko.dto.EmploymentDto;
import app.gozenko.dto.PassportDto;
import app.gozenko.enums.Gender;
import app.gozenko.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    @Column(name = "last_name", columnDefinition = "varchar(30)")
    private String lastName;
    @Column(name = "first_name", columnDefinition = "varchar(30)")
    private String firstName;
    @Column(name = "middle_name", columnDefinition = "varchar(30)")
    private String middleName;
    @Column(name = "birthday", columnDefinition = "date")
    private LocalDate birthday;
    @Column(name = "email", columnDefinition = "varchar(50)")
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "varchar(30)")
    private Gender gender;
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", columnDefinition = "varchar(30)")
    private MaritalStatus maritalStatus;
    @Column(name = "dependent_amount", columnDefinition = "int")
    private Integer dependentAmount;
    @Column(name = "account_number", columnDefinition = "varchar(30)")
    private String accountNumber;
    //TODO: верные связи - ?
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "passport", columnDefinition = "jsonb")
    private PassportDto passport;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "employment", columnDefinition = "jsonb")
    private EmploymentDto employment;

}
