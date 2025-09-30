package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.alphacode.alphacodepaymentservice.entity.LicenseKey;

import java.util.UUID;

public interface LicenseKeyRepository extends JpaRepository<LicenseKey, UUID> {
}
