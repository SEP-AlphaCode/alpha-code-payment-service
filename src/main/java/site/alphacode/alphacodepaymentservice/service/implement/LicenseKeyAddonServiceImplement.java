package site.alphacode.alphacodepaymentservice.service.implement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyAddonDto;
import site.alphacode.alphacodepaymentservice.dto.request.create.CreateLincenseKeyAddon;
import site.alphacode.alphacodepaymentservice.entity.LicenseKeyAddon;
import site.alphacode.alphacodepaymentservice.mapper.LicenseKeyAddonMapper;
import site.alphacode.alphacodepaymentservice.repository.LicenseKeyAddonRepository;
import site.alphacode.alphacodepaymentservice.repository.LicenseKeyRepository;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyAddonService;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LicenseKeyAddonServiceImplement implements LicenseKeyAddonService {
    private final LicenseKeyAddonRepository licenseKeyAddonRepository;
    private final LicenseKeyRepository licenseKeyRepository;
    private final LicenseKeyService licenseKeyService;

    @Override
    @Transactional
    @CachePut(value = "license_key_addon", key = "#result.id")
    public LicenseKeyAddonDto create(CreateLincenseKeyAddon createLincenseKeyAddon) {
        var entity = new LicenseKeyAddon();
        entity.setAddonId(createLincenseKeyAddon.getAddonId());
        entity.setLicenseKeyId(createLincenseKeyAddon.getLicenseKeyId());
        entity.setStatus(createLincenseKeyAddon.getStatus());
        entity.setCreatedDate(LocalDateTime.now());
        entity = licenseKeyAddonRepository.save(entity);
        return LicenseKeyAddonMapper.toDto(entity);
    }

    @Override
    @Cacheable(value = "license_key_addon", key = "{#id}")
    public LicenseKeyAddonDto getById(UUID id) {
        var licenseKeyAddon = licenseKeyAddonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy LicenseKeyAddon với id: " + id));
        return LicenseKeyAddonMapper.toDto(licenseKeyAddon);
    }

    @Override
    public boolean isActiveAddonForLicenseKey(UUID addonId, String key) {
        var licenseKey = licenseKeyRepository.findLicenseKeyByKey(key).orElseThrow(() -> new IllegalArgumentException("Key không hợp lệ"));
        var licenseKeyAddon = licenseKeyAddonRepository.findByAddonIdAndLicenseKeyIdAndStatus(addonId, licenseKey.getId(), 1);
        return licenseKeyAddon.isPresent();
    }

    @Override
    @Transactional
    @CachePut(value = "license_key_addon", key = "{#id}")
    public boolean activate(UUID id){
        var licenseKeyAddon = licenseKeyAddonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy LicenseKeyAddon với id: " + id));
        log.info("Activating LicenseKeyAddon id={}", id);
        licenseKeyAddon.setStatus(1); // Active
        licenseKeyAddon.setLastUpdated(LocalDateTime.now());
        licenseKeyAddonRepository.save(licenseKeyAddon);
        log.info("LicenseKeyAddon id={}", id);
        log.info("LicenseKeyAddon status={}", licenseKeyAddon.getStatus());
        return true;
    }

    @Override
    public boolean isActiveAddonForLicenseKey(Integer category, String key) {
        return licenseKeyAddonRepository
                .findActiveAddonByCategory(category, key, 1)
                .isPresent();
    }

    @Override
    public boolean validateAddon(site.alphacode.alphacodepaymentservice.dto.request.ValidateAddonRequest request) {
        // 1) Check license key valid + đúng account
        var result = licenseKeyService.validateLicense(request.getKey(), request.getAccountId());
        if (!result) {
            return false;
        }

        // 2) Check addon by category
        return isActiveAddonForLicenseKey(request.getCategory(), request.getKey());
    }

    @Override
    public boolean validateAddon(String accessToken, String key, UUID accountId, Integer category) {
        // Extract accountId from token if provided
        UUID finalAccountId = accountId;
        if (accessToken != null && !accessToken.isEmpty()) {
            String accountIdFromToken = extractAccountIdFromToken(accessToken);
            if (accountIdFromToken != null) {
                finalAccountId = UUID.fromString(accountIdFromToken);
            }
        }

        // Validate required parameters
        if (key == null || finalAccountId == null || category == null) {
            return false;
        }

        // 1) Check license key valid + đúng account
        var result = licenseKeyService.validateLicense(key, finalAccountId);
        if (!result) {
            return false;
        }

        // 2) Check addon by category
        return isActiveAddonForLicenseKey(category, key);
    }

    /**
     * Helper method to extract accountId from JWT token (field "id")
     */
    private String extractAccountIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Split token and decode payload
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(payload);

            // Extract "id" field from token
            if (node.has("id")) {
                return node.get("id").asText();
            }

            return null;
        } catch (Exception e) {
            log.error("Failed to extract accountId from token", e);
            return null;
        }
    }
}
