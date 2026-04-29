package app.gozenko.entity;

import app.gozenko.dto.PaymentScheduleElementDto;
import app.gozenko.enums.CreditStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "credit")
@Data
@ToString(exclude = {"statement"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    @Column(name = "amount", columnDefinition = "decimal")
    private BigDecimal amount;
    @Column(name = "term", columnDefinition = "int")
    private Integer term;
    @Column(name = "monthly_payment", columnDefinition = "decimal")
    private BigDecimal monthlyPayment;
    @Column(name = "rate", columnDefinition = "decimal")
    private BigDecimal rate;
    @Column(name = "psk", columnDefinition = "decimal")
    private BigDecimal psk;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_schedule", columnDefinition = "jsonb")
    private List<PaymentScheduleElementDto> paymentSchedule;
    @Column(name = "insurance_enabled", columnDefinition = "boolean")
    private Boolean isInsuranceEnabled;
    @Column(name = "salary_client", columnDefinition = "boolean")
    private Boolean isSalaryClient;
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_status", columnDefinition = "varchar(30)")
    private CreditStatus creditStatus;

    @OneToOne(mappedBy = "credit", fetch = FetchType.EAGER)
    private Statement statement;
}
