package site.alphacode.alphacodepaymentservice.service.implement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyDto;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyInfo;
import site.alphacode.alphacodepaymentservice.entity.LicenseKey;
import site.alphacode.alphacodepaymentservice.enums.LicenseKeyEnum;
import site.alphacode.alphacodepaymentservice.mapper.LicenseKeyMapper;
import site.alphacode.alphacodepaymentservice.repository.LicenseKeyRepository;
import site.alphacode.alphacodepaymentservice.service.KeyPriceService;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LicenseKeyServiceImplement implements LicenseKeyService {

    private final LicenseKeyRepository licenseKeyRepository;
    private final KeyPriceService keyPriceService;

    /**
     * Tạo license mới, cache riêng cho LicenseKeyDto
     */
    @Override
    @CachePut(value = "license_key_dto", key = "#accountId")
    @CacheEvict(value = {"key_string", "license_key_dto", "license_key_info"}, allEntries = true)
    public LicenseKeyDto createLicense(UUID accountId) {
        String key;
        do {
            key = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        } while (licenseKeyRepository.existsByKey(key));

        var keyPrice = keyPriceService.getKeyPrice();

        LicenseKey license = LicenseKey.builder()
                .key(key)
                .accountId(accountId)
                .purchaseDate(LocalDateTime.now())
                .status(LicenseKeyEnum.INACTIVE.getCode())
                .keyPriceId(keyPrice.getId())
                .build();

        var created = licenseKeyRepository.save(license);
        return LicenseKeyMapper.toDto(created);
    }

    /**
     * Validate license (backward compatible)
     */
    @Override
    public boolean validateLicense(String key, UUID accountId) {
        return licenseKeyRepository.findByKeyAndStatus(key, 1)
                .map(license -> license.getAccountId().equals(accountId))
                .orElse(false);
    }

    /**
     * Validate license with cookie support
     */
    @Override
    public boolean validateLicenseKey(String accessToken, String key, UUID accountId) {
        // Extract accountId from token if provided
        UUID finalAccountId = accountId;
        if (accessToken != null && !accessToken.isEmpty()) {
            String accountIdFromToken = extractAccountIdFromToken(accessToken);
            if (accountIdFromToken != null) {
                finalAccountId = UUID.fromString(accountIdFromToken);
            }
        }

        // Validate required parameters
        if (key == null || finalAccountId == null) {
            return false;
        }

        // Use existing validation logic
        return validateLicense(key, finalAccountId);
    }

    /**
     * Lấy key dạng String, cache riêng
     */
    @Override
    @Cacheable(value = "key_string", key = "#accountId", unless = "#result == null")
    public String getKeyByAccountId(UUID accountId) {
        return licenseKeyRepository.findByAccountIdAndStatus(accountId, 1)
                .map(LicenseKey::getKey)
                .orElse(null);
    }

    /**
     * Lấy LicenseKeyDto, cache riêng
     */
    @Override
    @Cacheable(value = "license_key_dto", key = "#accountId")
    public LicenseKeyDto getLicenseByAccountId(UUID accountId){
        var license = licenseKeyRepository.findByAccountIdAndStatus(accountId, 1)
                .orElseThrow(() -> new RuntimeException("KHÔNG TỒN TẠI"));
        return LicenseKeyMapper.toDto(license);
    }

    /**
     * Vô hiệu hóa license
     */
    @Override
    @CacheEvict(value = {"key_string", "license_key_dto", "license_key_info"}, key = "#accountId")
    public void deactivateLicense(String key) {
        licenseKeyRepository.findByKeyAndStatus(key, 1)
                .ifPresent(license -> {
                    license.setStatus(LicenseKeyEnum.INACTIVE.getCode());
                    licenseKeyRepository.save(license);
                });
    }

    /**
     * Kích hoạt license
     */
    @Override
    @CacheEvict(value = {"key_string", "license_key_dto", "license_key_info"}, allEntries = true)
    @Transactional
    public void activateLicense(UUID id){
        var license = licenseKeyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KHÔNG TỒN TẠI"));
        license.setStatus(LicenseKeyEnum.ACTIVE.getCode());
        licenseKeyRepository.save(license);
    }

    /**
     * Lấy thông tin license
     */
    @Override
    @Cacheable(value = "license_key_info", key = "#accountId")
    public LicenseKeyInfo getLicenseInfoByAccountId(UUID accountId){
        var license = licenseKeyRepository.findByAccountIdAndStatus(accountId, 1);

        LicenseKeyInfo licenseKeyInfo = new LicenseKeyInfo();
        if(license.isEmpty()){
            licenseKeyInfo.setHasPurchased(false);
            return licenseKeyInfo;
        }
        licenseKeyInfo.setHasPurchased(true);
        licenseKeyInfo.setPurchaseDate(license.get().getPurchaseDate());
        return licenseKeyInfo;
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

