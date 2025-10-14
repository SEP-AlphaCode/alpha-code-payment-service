package site.alphacode.alphacodepaymentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;
import site.alphacode.alphacodepaymentservice.enums.SubscriptionPlanEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRuleDto extends BaseEntityDto implements Serializable {
    private UUID id;
    private String code;
    private Integer cost;
    private String note;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return SubscriptionPlanEnum.fromCode(this.getStatus());
    }
}
