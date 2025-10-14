package site.alphacode.alphacodepaymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodepaymentservice.base.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "token_rule")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TokenRule extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "note")
    private String note;
}
