package site.alphacode.alphacodepaymentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;
import site.alphacode.alphacodepaymentservice.enums.PaymentCategoryEnum;
import site.alphacode.alphacodepaymentservice.enums.PaymentStatusEnum;

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
    private UUID planId;
    private UUID keyId;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return PaymentStatusEnum.fromCode(this.getStatus());
    }
    @JsonProperty(value = "categoryText")
    public String getCategoryText() {
        return PaymentCategoryEnum.fromCode(this.getCategory());
    }
}
