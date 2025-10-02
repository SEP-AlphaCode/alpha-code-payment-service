package site.alphacode.alphacodepaymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;

import java.io.Serializable;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto extends BaseEntityDto implements Serializable {
    private UUID id;
    private Integer amount;
    private Integer category;
    private String paymentMethod;
    private UUID accountId;
    private UUID addonId;
    private UUID courseId;
    private UUID bundleId;
    private UUID subscriptionId;
}
