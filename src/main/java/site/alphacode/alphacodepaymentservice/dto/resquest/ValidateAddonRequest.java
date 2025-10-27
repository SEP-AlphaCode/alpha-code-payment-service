package site.alphacode.alphacodepaymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class ValidateAddonRequest {

    @NotBlank(message = "Key không được để trống")
    private String key;

    @NotNull(message = "AccountId không được null")
    private UUID accountId;

    @NotNull(message = "Category không được null")
    @Positive(message = "Category phải là số nguyên dương")
    private Integer category;
}
