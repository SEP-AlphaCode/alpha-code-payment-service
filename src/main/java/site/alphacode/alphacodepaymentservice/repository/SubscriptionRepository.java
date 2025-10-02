package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.alphacode.alphacodepaymentservice.entity.Subscription;

import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
}
