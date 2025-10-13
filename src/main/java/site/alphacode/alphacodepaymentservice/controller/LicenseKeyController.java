package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public void deactivateLicenseKeyByAccountId(@RequestParam String key) {
        licenseKeyService.deactivateLicense(key);
    }
}
