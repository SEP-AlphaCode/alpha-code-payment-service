package site.alphacode.alphacodepaymentservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyDto;
import site.alphacode.alphacodepaymentservice.entity.LicenseKey;
import site.alphacode.alphacodepaymentservice.enums.LicenseKeyEnum;
import site.alphacode.alphacodepaymentservice.mapper.LicenseKeyMapper;
import site.alphacode.alphacodepaymentservice.repository.KeyPriceRepository;
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

    @Override
    @CachePut(value = "key", key = "#accountId")
    public String createLicense(UUID accountId) {
        String key = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        var keyPrice = keyPriceService.getKeyPrice();

        LicenseKey license = LicenseKey.builder()
                .key(key)
                .accountId(accountId)
                .purchaseDate(LocalDateTime.now())
                .status(LicenseKeyEnum.ACTIVE.getCode())
                .price(keyPrice.getPrice())
                .build();

        licenseKeyRepository.save(license);
        return key; // trả về key dạng string
    }

    @Override
    public String validateLicense(String key, UUID accountId) {
        return licenseKeyRepository.findByKeyAndStatus(key, 1)
                .map(license -> {
                    if (!license.getAccountId().equals(accountId)) {
                        return "KHÔNG PHÙ HỢP ACCOUNT";
                    }
                    return LicenseKeyEnum.fromCode(license.getStatus());
                })
                .orElse("KHÔNG TỒN TẠI");
    }

    @Override
    @Cacheable(value = "key", key = "#accountId")
    public String getKeyByAccountId(UUID accountId) {
        return licenseKeyRepository.findByAccountIdAndStatus(accountId, 1)
                .map(LicenseKey::getKey)
                .orElse("KHÔNG TỒN TẠI");
    }

    @Override
    @Cacheable(value = "license_key", key = "#accountId")
    public LicenseKeyDto getLicenseByAccountId(UUID accountId){
        var license = licenseKeyRepository.findByAccountIdAndStatus(accountId, 1)
                .orElseThrow(() -> new RuntimeException("KHÔNG TỒN TẠI"));
        return LicenseKeyMapper.toDto(license);
    }

    @Override
    @CacheEvict(value = {"key", "license_key"}, key = "#accountId")
    public String deactivateLicense(String key) {
        return licenseKeyRepository.findByKeyAndStatus(key, 1)
                .map(license -> {
                    license.setStatus(LicenseKeyEnum.INACTIVE.getCode());
                    licenseKeyRepository.save(license);
                    return LicenseKeyEnum.fromCode(license.getStatus());
                })
                .orElse("KHÔNG TỒN TẠI");
    }
}
