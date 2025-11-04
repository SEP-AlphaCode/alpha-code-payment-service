package site.alphacode.alphacodepaymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseKeyInfo {
    private Boolean hasPurchased;
    private LocalDateTime purchaseDate;
}
