package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.alphacode.alphacodepaymentservice.entity.LicenseKey;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseKeyRepository extends JpaRepository<LicenseKey, UUID> {

    // Lấy key theo key string, chỉ ACTIVE
    Optional<LicenseKey> findByKeyAndStatus(String key, Integer status);

    // Lấy key theo accountId, chỉ ACTIVE
    Optional<LicenseKey> findByAccountIdAndStatus(UUID accountId, Integer status);

    // Nếu muốn lấy tất cả key của account (cả active/inactive)
    Optional<LicenseKey> findByAccountId(UUID accountId);

    boolean existsByKey(String key);

    Optional<LicenseKey> findLicenseKeyByKey(String key);
}

