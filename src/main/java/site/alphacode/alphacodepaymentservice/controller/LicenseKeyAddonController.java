package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyAddonService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/license-key-addons")
@RequiredArgsConstructor
@Tag(name = "License Key Addons", description = "License Key Addon management APIs")
public class LicenseKeyAddonController {
    private final LicenseKeyAddonService licenseKeyAddonService;

    @GetMapping
    @Operation(summary = "Check if addon is available for license key")
    @PreAuthorize("hasAnyAuthority('ROLE_Parent', 'ROLE_Children')")
    public boolean isAddonAvailable(@RequestParam UUID addonId, @RequestParam String key) {
        return licenseKeyAddonService.isActiveAddonForLicenseKey(addonId, key);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateAddon(@RequestBody site.alphacode.alphacodepaymentservice.dto.request.ValidateAddonRequest request) {
        boolean allowed = licenseKeyAddonService.validateAddon(request);
        return ResponseEntity.ok(allowed);
    }
}
