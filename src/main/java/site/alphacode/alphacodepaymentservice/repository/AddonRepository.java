package site.alphacode.alphacodepaymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.alphacode.alphacodepaymentservice.entity.Addon;

import java.util.UUID;

public interface AddonRepository extends JpaRepository<Addon, UUID> {
}
