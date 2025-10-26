package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.alphacode.alphacodepaymentservice.entity.KeyPrice;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KeyPriceRepository extends JpaRepository<KeyPrice, UUID> {
    Optional<KeyPrice> findTopByOrderByCreatedDateDesc();

    @Modifying
    @Query("UPDATE KeyPrice k SET k.status = 0 WHERE k.status = 1")
    void deactivateAllActiveKeys();

    Optional<KeyPrice> findTopByStatusOrderByCreatedDateDesc(Integer status);
}
