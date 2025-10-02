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
@Table(name = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "category", nullable = false)
    private Integer category;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "order_code", unique = true, nullable = false)
    private Long orderCode;

    @Column(name = "note")
    private String note;

    @Column(name = "account_id", nullable = false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(name = "addon_id", columnDefinition = "uuid")
    private UUID addonId;

    @Column(name = "bundle_id", columnDefinition = "uuid")
    private UUID bundleId;

    @Column(name = "course_id", columnDefinition = "uuid")
    private UUID courseId;

    @Column(name = "subscription_id", columnDefinition = "uuid")
    private UUID subscriptionId;
}
