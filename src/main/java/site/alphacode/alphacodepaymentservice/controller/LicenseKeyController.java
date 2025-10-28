package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyDto;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/license-keys")
@RequiredArgsConstructor
@Tag(name = "License Keys", description = "License Key management APIs")
public class LicenseKeyController {
    private final LicenseKeyService licenseKeyService;

    @DeleteMapping("deactivate")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    @Operation(summary = "Deactivate license key by account id")
    public void deactivateLicenseKeyByAccountId(@RequestParam String key) {
        licenseKeyService.deactivateLicense(key);
    }

    @GetMapping("by-account/{accountId}")
    @Operation(summary = "Get license key by account id")
    public String getLicenseKeyByAccountId(@PathVariable UUID accountId) {
        return licenseKeyService.getKeyByAccountId(accountId);
    }

    @GetMapping("get-license-by-account/{accountId}")
    @Operation(summary = "Get license by account id")
    public LicenseKeyDto getLicenseKey(@PathVariable UUID accountId) {
        return licenseKeyService.getLicenseByAccountId(accountId);
    }

    @GetMapping("validate-key")
    @Operation(summary = "Validate license key")
    public boolean validateLicenseKey(@RequestParam String key, @RequestParam UUID accountId) {
        return licenseKeyService.validateLicense(key, accountId);
    }
}
