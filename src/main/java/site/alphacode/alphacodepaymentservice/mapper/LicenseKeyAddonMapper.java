package site.alphacode.alphacodepaymentservice.mapper;

import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyAddonDto;
import site.alphacode.alphacodepaymentservice.entity.LicenseKeyAddon;

public class LicenseKeyAddonMapper {
    public static LicenseKeyAddonDto toDto(LicenseKeyAddon licenseKeyAddon) {
        if (licenseKeyAddon == null) {
            return null;
        }

        return LicenseKeyAddonDto.builder()
                .id(licenseKeyAddon.getId())
                .licenseKeyId(licenseKeyAddon.getLicenseKeyId())
                .addonId(licenseKeyAddon.getAddonId())
                .status(licenseKeyAddon.getStatus())
                .createdDate(licenseKeyAddon.getCreatedDate())
                .lastUpdated(licenseKeyAddon.getLastUpdated())
                .build();
    }
}
