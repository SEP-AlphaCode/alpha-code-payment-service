package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.alphacode.alphacodepaymentservice.entity.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByAccountIdAndStatus(UUID accountId, Integer status);
    Optional<Subscription> findFirstByAccountIdAndStatusOrderByStartDateDesc(UUID accountId, Integer status);
}
