package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodepaymentservice.entity.TokenRule;

import java.util.Optional;
import java.util.UUID;

public interface TokenRuleRepository extends JpaRepository<TokenRule, UUID> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID id);

    boolean existsByCodeAndStatus(String code, Integer status);

    Optional<TokenRule> findByIdAndStatus(UUID id, Integer status);

    @Query("SELECT rc from TokenRule rc WHERE rc.status = 1 AND (:searchTerm IS NULL OR :searchTerm = '' \n" +
            "              OR LOWER(rc.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))\n" +
            "              OR LOWER(rc.note) LIKE LOWER(CONCAT('%', :searchTerm, '%')))\n" +
            "       ORDER BY rc.createdDate DESC")
    Page<TokenRule> findAll(@Param("searchTerm") String searchTerm, Pageable pageable);
}
