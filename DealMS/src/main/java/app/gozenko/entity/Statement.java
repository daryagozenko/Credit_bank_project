package app.gozenko.entity;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.dto.StatementStatusHistoryDto;
import app.gozenko.enums.StatementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "statement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statement {
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(50)")
    private StatementStatus status;
    @Column(name = "creation_date", columnDefinition = "timestamp")
    private LocalDateTime creationDate;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private List<LoanOfferDto> appliedOffer;
    @Column(name = "sign_date", columnDefinition = "timestamp")
    private LocalDateTime signDate;
    @Column(name = "ses_code", columnDefinition = "int")
    private Integer sesCode;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", columnDefinition = "jsonb")
    private List<StatementStatusHistoryDto> statusHistory;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "credit_id")
    private Credit credit;
}
