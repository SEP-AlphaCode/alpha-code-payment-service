package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyDto;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyInfo;

import java.util.UUID;

public interface LicenseKeyService {
    // Tạo key trọn đời và trả về key (string)
    LicenseKeyDto createLicense(UUID accountId);

    String getKeyByAccountId(UUID accountId);

    LicenseKeyDto getLicenseByAccountId(UUID accountId);

    // Validate key: trả về trạng thái dạng string
    boolean validateLicense(String key, UUID accountId);

    // Vô hiệu hóa key
    void deactivateLicense(String key);
    void activateLicense(UUID id);
    LicenseKeyInfo getLicenseInfoByAccountId(UUID accountId);
}
