package site.alphacode.alphacodepaymentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.enums.LicenseKeyEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseKeyDto implements Serializable {
    private UUID id;
    private String key;
    private UUID accountId;
    private Integer price;
    private LocalDateTime purchaseDate;
    private Integer status;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return LicenseKeyEnum.fromCode(this.getStatus());
    }
}
