package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
    public void activate(UUID id){
        var licenseKeyAddon = licenseKeyAddonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy LicenseKeyAddon với id: " + id));
        licenseKeyAddon.setStatus(1); // Active
        licenseKeyAddon.setLastUpdated(LocalDateTime.now());
        licenseKeyAddonRepository.save(licenseKeyAddon);
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


}
