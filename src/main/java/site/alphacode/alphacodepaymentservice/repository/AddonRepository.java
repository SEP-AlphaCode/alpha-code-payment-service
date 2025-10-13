package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.alphacode.alphacodepaymentservice.entity.Addon;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddonRepository extends JpaRepository<Addon, UUID> {
    boolean existsAddonByName(String name);

    @Query("SELECT a FROM Addon a WHERE a.id = :id AND a.status <> 0")
    Optional<Addon> findNoneDeletedById(@Param("id") UUID id);

    @Query("SELECT a FROM Addon a WHERE a.name = :name AND a.status <> 0")
    Optional<Addon> findNoneDeletedByName(@Param("name") String name);
}
