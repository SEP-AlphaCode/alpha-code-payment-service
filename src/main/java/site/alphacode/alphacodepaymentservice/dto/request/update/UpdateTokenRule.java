package site.alphacode.alphacodepaymentservice.dto.request.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;

import java.util.UUID;

@Data
public class UpdateTokenRule extends BaseEntityDto {
    @NotNull(message = "Id token rule là bắt buộc")
    private UUID id;

    @NotBlank(message = "Code không được để trống")
    @Size(max = 50, message = "Code không được dài hơn 50 ký tự")
    private String code;

    @NotNull(message = "Cost là bắt buộc")
    @Positive(message = "Cost phải lớn hơn 0")
    private Integer cost;

    @NotNull(message = "Note là bắt buộc")
    private String note;
}
