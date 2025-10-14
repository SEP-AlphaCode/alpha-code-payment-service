package site.alphacode.alphacodepaymentservice.dto.resquest.patch;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;

@Data
public class PatchTokenRule extends BaseEntityDto {
    @Size(max = 50, message = "Code không được dài hơn 50 ký tự")
    private String code;

    @Positive(message = "Cost phải lớn hơn 0")
    private Integer cost;

    private String note;
}
