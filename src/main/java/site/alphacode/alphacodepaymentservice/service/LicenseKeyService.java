package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyDto;

import java.util.UUID;

public interface LicenseKeyService {
    // Tạo key trọn đời và trả về key (string)
    String createLicense(UUID accountId);

    String getKeyByAccountId(UUID accountId);

    LicenseKeyDto getLicenseByAccountId(UUID accountId);

    // Validate key: trả về trạng thái dạng string
    String validateLicense(String key, UUID accountId);

    // Vô hiệu hóa key
    String deactivateLicense(String key);
}
