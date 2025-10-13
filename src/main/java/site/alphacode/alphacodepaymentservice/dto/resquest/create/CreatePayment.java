package site.alphacode.alphacodepaymentservice.dto.resquest.create;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePayment {
    @NotNull(message = "ID người dùng là bắt buộc")
    private UUID accountId;

    private UUID addonId;

    private UUID bundleId;

    private UUID courseId;

    private UUID subscriptionId;

    private UUID keyId;
}
