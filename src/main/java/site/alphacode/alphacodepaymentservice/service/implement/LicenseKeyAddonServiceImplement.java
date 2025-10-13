package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyAddonDto;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateLincenseKeyAddon;
import site.alphacode.alphacodepaymentservice.entity.LicenseKeyAddon;
import site.alphacode.alphacodepaymentservice.mapper.LicenseKeyAddonMapper;
import site.alphacode.alphacodepaymentservice.repository.LicenseKeyAddonRepository;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyAddonService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LicenseKeyAddonServiceImplement implements LicenseKeyAddonService {
    private final LicenseKeyAddonRepository licenseKeyAddonRepository;

    @Override
    @Transactional
    @CachePut(value = "license_key_addon", key = "#result.id")
    public LicenseKeyAddonDto create(CreateLincenseKeyAddon createLincenseKeyAddon) {
        var entity = new LicenseKeyAddon();
        entity.setAddonId(createLincenseKeyAddon.getAddonId());
        entity.setLicenseKeyId(createLincenseKeyAddon.getLicenseKeyId());
        entity.setStatus(1);
        entity.setCreatedDate(LocalDateTime.now());
        entity = licenseKeyAddonRepository.save(entity);
        return LicenseKeyAddonMapper.toDto(entity);
    }
}
