package site.alphacode.alphacodepaymentservice.dto.request.create;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateLincenseKeyAddon {
    @NotNull(message = "ID addon là bắt buộc")
    private UUID addonId;
    @NotNull(message = "ID license key là bắt buộc")
    private UUID licenseKeyId;
    @NotNull(message = "Trạng thái là bắt buộc")
    Integer status;
}
