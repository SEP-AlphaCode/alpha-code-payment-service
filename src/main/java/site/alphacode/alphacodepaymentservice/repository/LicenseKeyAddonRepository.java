package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.alphacode.alphacodepaymentservice.entity.LicenseKeyAddon;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseKeyAddonRepository extends JpaRepository<LicenseKeyAddon, UUID> {
    boolean existsByLicenseKeyIdAndAddonIdAndStatus(UUID licenseKeyId, UUID addonId, Integer status);

    Optional<LicenseKeyAddon> findByAddonIdAndLicenseKeyIdAndStatus(UUID addonId, UUID licenseKeyId, Integer status);

    @Query("""
    SELECT lka FROM LicenseKeyAddon lka
    JOIN lka.addon a
    JOIN lka.licenseKey lk
    WHERE a.category = :category
      AND lk.key = :licenseKey
      AND lka.status = :status
""")
    Optional<LicenseKeyAddon> findActiveAddonByCategory(
            Integer category,
            String licenseKey,
            Integer status
    );
}
