package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyAddonDto;
import site.alphacode.alphacodepaymentservice.dto.request.create.CreateLincenseKeyAddon;

import java.util.UUID;

public interface LicenseKeyAddonService {
    LicenseKeyAddonDto create(CreateLincenseKeyAddon createLincenseKeyAddon);
    boolean isActiveAddonForLicenseKey(UUID addonId, String key);
    LicenseKeyAddonDto getById(UUID id);
    boolean activate(UUID id);
    boolean isActiveAddonForLicenseKey(Integer category, String key);
    boolean validateAddon(site.alphacode.alphacodepaymentservice.dto.request.ValidateAddonRequest request);

}
