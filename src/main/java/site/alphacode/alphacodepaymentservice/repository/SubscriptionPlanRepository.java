package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.alphacode.alphacodepaymentservice.entity.SubscriptionPlan;

import java.util.UUID;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    SubscriptionPlan findByNameAndStatus(String name, Integer status);

    Page<SubscriptionPlan> findAllByNameContainingAndStatus(String name, Integer status, Pageable pageable);

    @Query(
            "SELECT sp FROM SubscriptionPlan sp WHERE sp.name LIKE %:name% And sp.status <> 0"
    )
    Page<SubscriptionPlan> findAllByNameContaining(String name, Pageable pageable);
}
