package app.gozenko.DealMS.entity;

import app.gozenko.DealMS.dto.LoanOfferDto;
import app.gozenko.DealMS.dto.StatementStatusHistoryDto;
import app.gozenko.DealMS.enums.StatementStatus;
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
    //TODO: applied_offer (jsonb) - правильно?
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private List<LoanOfferDto> appliedOffer;
    @Column(name = "sign_date", columnDefinition = "timestamp")
    private LocalDateTime signDate;
    //TODO: ses_code - ?
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", columnDefinition = "jsonb")
    private List<StatementStatusHistoryDto> statusHistory;

    //TODO: (FK) client_id/credit_id - верно?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "credit_id")
    private Credit credit;
}
