package site.alphacode.alphacodepaymentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;
import site.alphacode.alphacodepaymentservice.enums.AddonEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddonDto extends BaseEntityDto implements Serializable  {
    private UUID id;
    private String name;
    private Integer price;
    private Integer category;
    private String description;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return AddonEnum.fromCode(this.getStatus());
    }
    @JsonProperty(value = "categoryText")
    public String getCategoryText() {
        return AddonEnum.fromCode(this.getCategory());
    }
}
