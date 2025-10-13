package site.alphacode.alphacodepaymentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;
import site.alphacode.alphacodepaymentservice.enums.LicenseKeyAddonEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseKeyAddonDto extends BaseEntityDto implements Serializable {
    private UUID id;
    private UUID licenseKeyId;
    private UUID addonId;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return LicenseKeyAddonEnum.fromCode(this.getStatus());
    }
}
