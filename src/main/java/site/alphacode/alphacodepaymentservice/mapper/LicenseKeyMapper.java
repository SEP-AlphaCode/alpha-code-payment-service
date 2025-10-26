package site.alphacode.alphacodepaymentservice.mapper;

import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyDto;
import site.alphacode.alphacodepaymentservice.entity.LicenseKey;

public class LicenseKeyMapper {
    public static LicenseKeyDto toDto(LicenseKey licenseKey){
        if(licenseKey == null){
            return null;
        } else {
            return LicenseKeyDto.builder()
                    .id(licenseKey.getId())
                    .key(licenseKey.getKey())
                    .status(licenseKey.getStatus())
                    .accountId(licenseKey.getAccountId())
                    .purchaseDate(licenseKey.getPurchaseDate())
                    .build();
        }
    }
}
