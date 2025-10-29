package site.alphacode.alphacodepaymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodepaymentservice.base.BaseEntity;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "subscription_plan")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SubscriptionPlan extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "billing_cycle", nullable = false)
    private Integer billingCycle; // in months

    @Column(name = "is_recommended", nullable = false)
    private Boolean isRecommended;

//    @Column(name = "quota", nullable = false)
//    private Integer quota; // e.g., number of courses or features included

//    @Column(name = "duration_day")
//    private Integer durationDay; // Duration of the subscription in day
}
