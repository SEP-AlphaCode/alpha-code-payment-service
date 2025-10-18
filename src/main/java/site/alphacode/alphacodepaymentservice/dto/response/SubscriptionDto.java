package site.alphacode.alphacodepaymentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;
import site.alphacode.alphacodepaymentservice.enums.SubscriptionEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto extends BaseEntityDto implements Serializable {
    private UUID id;
    private UUID planId;
    private UUID accountId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return SubscriptionEnum.fromCode(this.getStatus());
    }
}
