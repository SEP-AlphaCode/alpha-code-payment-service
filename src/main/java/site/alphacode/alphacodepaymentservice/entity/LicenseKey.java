package site.alphacode.alphacodepaymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "license_key")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LicenseKey {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "key", nullable = false, unique = true)
    private String key;

    @Column(name = "account_id", nullable = false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(name = "key_price_id", nullable = false)
    private UUID keyPriceId;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "status", nullable = false)
    private Integer status;

    @OneToMany(mappedBy = "licenseKey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<LicenseKeyAddon> licenseKeyAddons;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_price_id", insertable = false, updatable = false)
    private KeyPrice keyPrice;
}
