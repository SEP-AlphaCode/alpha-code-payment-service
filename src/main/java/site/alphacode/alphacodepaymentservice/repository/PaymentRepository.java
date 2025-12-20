package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.alphacode.alphacodepaymentservice.entity.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrderCode(Long orderCode);

    @Query("""
    SELECT p
    FROM Payment p
    WHERE p.accountId = :accountId
      AND p.category = :category
      AND p.status = :status
      AND (
        (:category = 1 AND p.courseId = :serviceId) OR
        (:category = 2 AND p.bundleId = :serviceId) OR
        (:category = 3 AND p.licenseKeyAddonId = :serviceId) OR
        (:category = 4 AND p.planId = :serviceId) OR
        (:category = 5 AND p.licenseKeyId = :serviceId)
      )
    ORDER BY p.createdDate DESC
""")
    Optional<Payment> findFirstPendingByAccountAndService(
            @Param("accountId") UUID accountId,
            @Param("category") Integer category,
            @Param("serviceId") UUID serviceId,
            @Param("status") Integer status
    );

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE MONTH(p.createdDate) = :month
          AND YEAR(p.createdDate) = :year
    """)
    Long sumRevenueByMonth(
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
   SELECT YEAR(p.createdDate), MONTH(p.createdDate), SUM(p.amount)
   FROM Payment p
   GROUP BY YEAR(p.createdDate), MONTH(p.createdDate)
""")
    List<Object[]> sumRevenueGroupByMonth();
}
