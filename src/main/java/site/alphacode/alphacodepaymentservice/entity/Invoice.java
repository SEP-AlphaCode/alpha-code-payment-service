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
@Table(name = "invoice")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Invoice extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "payment_id", columnDefinition = "uuid", nullable = false)
    private UUID paymentId;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "tax_amount", nullable = false)
    private Integer taxAmount;

    @Column(name = "tax_rate", nullable = false)
    private Integer taxRate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Payment payment;
}
