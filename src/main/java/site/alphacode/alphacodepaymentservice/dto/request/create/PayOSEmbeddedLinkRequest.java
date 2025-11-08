package site.alphacode.alphacodepaymentservice.dto.request.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PayOSEmbeddedLinkRequest {
    @NotNull(message = "Tên sản phẩm là bắt buộc")
    private String name;

    @NotNull(message = "Số tiền là bắt buộc")
    @Min(value = 1, message = "Số tiền phải lớn hơn 0")
    private Integer price;
}
