package site.alphacode.alphacodepaymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodepaymentservice.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "plan_id", nullable = false, columnDefinition = "uuid")
    private UUID planId;

    @Column(name = "account_id", nullable = false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(name = "remaining_quota", nullable = false)
    private Integer remainingQuota;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SubscriptionPlan subscriptionPlan;
}
