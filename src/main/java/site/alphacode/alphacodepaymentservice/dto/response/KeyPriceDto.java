package site.alphacode.alphacodepaymentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.enums.AddonStatusEnum;
import site.alphacode.alphacodepaymentservice.enums.KeyPriceEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KeyPriceDto implements Serializable {
    private UUID id;
    private Integer price;
    private Integer status;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdDate;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return KeyPriceEnum.fromCode(this.getStatus());
    }
}
