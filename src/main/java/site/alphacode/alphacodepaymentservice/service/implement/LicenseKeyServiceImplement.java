package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyDto;
import site.alphacode.alphacodepaymentservice.entity.LicenseKey;
import site.alphacode.alphacodepaymentservice.enums.LicenseKeyEnum;
import site.alphacode.alphacodepaymentservice.mapper.LicenseKeyMapper;
import site.alphacode.alphacodepaymentservice.repository.LicenseKeyRepository;
import site.alphacode.alphacodepaymentservice.service.KeyPriceService;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LicenseKeyServiceImplement implements LicenseKeyService {

    private final LicenseKeyRepository licenseKeyRepository;
    private final KeyPriceService keyPriceService;

    /**
     * Tạo license mới, cache riêng cho LicenseKeyDto
     */
    @Override
    @CachePut(value = "license_key_dto", key = "#accountId")
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
     * Validate license
     */
    @Override
    public boolean validateLicense(String key, UUID accountId) {
        return licenseKeyRepository.findByKeyAndStatus(key, 1)
                .map(license -> license.getAccountId().equals(accountId))
                .orElse(false);
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
    @CacheEvict(value = {"key_string", "license_key_dto"}, key = "#accountId")
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
    @CacheEvict(value = {"key_string", "license_key_dto"}, allEntries = true)
    @Transactional
    public void activateLicense(UUID id){
        var license = licenseKeyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KHÔNG TỒN TẠI"));
        license.setStatus(LicenseKeyEnum.ACTIVE.getCode());
        licenseKeyRepository.save(license);
    }
}

