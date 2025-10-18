package site.alphacode.alphacodepaymentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;
import site.alphacode.alphacodepaymentservice.enums.SubscriptionEnum;
import site.alphacode.alphacodepaymentservice.enums.SubscriptionPlanEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDto extends BaseEntityDto implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private Integer price;
    private Integer billingCycle; // in months
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return SubscriptionPlanEnum.fromCode(this.getStatus());
    }
}
