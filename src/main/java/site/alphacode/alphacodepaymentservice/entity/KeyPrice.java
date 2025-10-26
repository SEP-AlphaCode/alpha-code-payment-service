package site.alphacode.alphacodepaymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodepaymentservice.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "key_price")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KeyPrice extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "price", nullable = false)
    private Integer price;

    @OneToMany(mappedBy = "keyPrice", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<LicenseKey> licenseKeys;
}
