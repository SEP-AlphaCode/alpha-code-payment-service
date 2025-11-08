package site.alphacode.alphacodepaymentservice.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTokenRule {
    @NotBlank(message = "Code không được để trống")
    @Size(max = 50, message = "Code không được dài hơn 50 ký tự")
    private String code;

    @NotNull(message = "Cost là bắt buộc")
    @Positive(message = "Cost phải lớn hơn 0")
    private Integer cost;

    @Size(max = 200, message = "Ghi chú không được dài quá 200 ký tự")
    private String note;
}
