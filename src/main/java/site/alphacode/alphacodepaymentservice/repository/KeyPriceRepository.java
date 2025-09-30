package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.alphacode.alphacodepaymentservice.entity.KeyPrice;

import java.util.Optional;
import java.util.UUID;

public interface KeyPriceRepository extends JpaRepository<KeyPrice, UUID> {
    Optional<KeyPrice> findTopByOrderByCreatedDateDesc();
}
