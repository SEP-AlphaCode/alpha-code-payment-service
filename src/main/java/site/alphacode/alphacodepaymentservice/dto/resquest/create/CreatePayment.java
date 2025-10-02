package site.alphacode.alphacodepaymentservice.dto.resquest.create;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePayment {
    @NotNull(message = "Số tiền là bắt buộc")
    @Min(value = 1, message = "Số tiền phải lớn hơn 0")
    private Integer amount;

    @NotNull(message = "Danh mục là bắt buộc")
    private Integer category;

    @NotNull(message = "ID người dùng là bắt buộc")
    private UUID accountId;

    private UUID addonId;

    private UUID bundleId;

    private UUID courseId;

    private UUID subscriptionId;
}
