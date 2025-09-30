package site.alphacode.alphacodepaymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodepaymentservice.base.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "license_key_addon")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LicenseKeyAddon extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "license_key_id", nullable = false, columnDefinition = "uuid")
    private UUID licenseKeyId;

    @Column(name = "addon_id", nullable = false, columnDefinition = "uuid")
    private UUID addonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addon_id", insertable = false, updatable = false)
    private Addon addon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_key_id", referencedColumnName = "id",insertable = false, updatable = false)
    private LicenseKey licenseKey;
}
