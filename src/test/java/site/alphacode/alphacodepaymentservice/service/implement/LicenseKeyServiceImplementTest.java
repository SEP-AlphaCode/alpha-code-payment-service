package site.alphacode.alphacodepaymentservice.service.implement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.alphacode.alphacodepaymentservice.dto.response.KeyPriceDto;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyDto;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyInfo;
import site.alphacode.alphacodepaymentservice.entity.LicenseKey;
import site.alphacode.alphacodepaymentservice.enums.LicenseKeyEnum;
import site.alphacode.alphacodepaymentservice.repository.LicenseKeyRepository;
import site.alphacode.alphacodepaymentservice.service.KeyPriceService;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LicenseKeyServiceImplement Tests")
class LicenseKeyServiceImplementTest {

    @Mock
    private LicenseKeyRepository licenseKeyRepository;

    @Mock
    private KeyPriceService keyPriceService;

    @InjectMocks
    private LicenseKeyServiceImplement licenseKeyService;

    private LicenseKey licenseKey;
    private UUID accountId;
    private UUID licenseKeyId;
    private UUID keyPriceId;
    private KeyPriceDto keyPriceDto;
    private String licenseKeyString;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        licenseKeyId = UUID.randomUUID();
        keyPriceId = UUID.randomUUID();
        licenseKeyString = "testkey12345678";

        keyPriceDto = KeyPriceDto.builder()
                .id(keyPriceId)
                .price(100000)
                .status(1)
                .build();

        licenseKey = LicenseKey.builder()
                .id(licenseKeyId)
                .key(licenseKeyString)
                .accountId(accountId)
                .keyPriceId(keyPriceId)
                .purchaseDate(LocalDateTime.now())
                .status(LicenseKeyEnum.ACTIVE.getCode())
                .build();
    }

    @Test
    @DisplayName("Validate license thành công khi key hợp lệ")
    void validateLicense_Success_WhenKeyIsValid() {
        // Given
        when(licenseKeyRepository.findByKeyAndStatus(licenseKeyString, 1))
                .thenReturn(Optional.of(licenseKey));

        // When
        boolean result = licenseKeyService.validateLicense(licenseKeyString, accountId);

        // Then
        assertTrue(result);
        verify(licenseKeyRepository, times(1)).findByKeyAndStatus(licenseKeyString, 1);
    }

    @Test
    @DisplayName("Validate license thất bại khi key không tồn tại")
    void validateLicense_False_WhenKeyNotFound() {
        // Given
        when(licenseKeyRepository.findByKeyAndStatus(licenseKeyString, 1))
                .thenReturn(Optional.empty());

        // When
        boolean result = licenseKeyService.validateLicense(licenseKeyString, accountId);

        // Then
        assertFalse(result);
        verify(licenseKeyRepository, times(1)).findByKeyAndStatus(licenseKeyString, 1);
    }

    @Test
    @DisplayName("Validate license thất bại khi accountId không khớp")
    void validateLicense_False_WhenAccountIdMismatch() {
        // Given
        UUID differentAccountId = UUID.randomUUID();
        when(licenseKeyRepository.findByKeyAndStatus(licenseKeyString, 1))
                .thenReturn(Optional.of(licenseKey));

        // When
        boolean result = licenseKeyService.validateLicense(licenseKeyString, differentAccountId);

        // Then
        assertFalse(result);
        verify(licenseKeyRepository, times(1)).findByKeyAndStatus(licenseKeyString, 1);
    }

    @Test
    @DisplayName("Validate license key với access token thành công")
    void validateLicenseKey_Success_WithAccessToken() {
        // Given
        String token = createTestJwtToken(accountId.toString());
        when(licenseKeyRepository.findByKeyAndStatus(licenseKeyString, 1))
                .thenReturn(Optional.of(licenseKey));

        // When
        boolean result = licenseKeyService.validateLicenseKey(token, licenseKeyString, accountId);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Validate license key thất bại khi key null")
    void validateLicenseKey_False_WhenKeyIsNull() {
        // When
        boolean result = licenseKeyService.validateLicenseKey(null, null, accountId);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Lấy key string theo accountId thành công")
    void getKeyByAccountId_Success() {
        // Given
        when(licenseKeyRepository.findByAccountIdAndStatus(accountId, 1))
                .thenReturn(Optional.of(licenseKey));

        // When
        String result = licenseKeyService.getKeyByAccountId(accountId);

        // Then
        assertNotNull(result);
        assertEquals(licenseKeyString, result);
        verify(licenseKeyRepository, times(1)).findByAccountIdAndStatus(accountId, 1);
    }

    @Test
    @DisplayName("Lấy key string trả về null khi không tìm thấy")
    void getKeyByAccountId_ReturnsNull_WhenNotFound() {
        // Given
        when(licenseKeyRepository.findByAccountIdAndStatus(accountId, 1))
                .thenReturn(Optional.empty());

        // When
        String result = licenseKeyService.getKeyByAccountId(accountId);

        // Then
        assertNull(result);
        verify(licenseKeyRepository, times(1)).findByAccountIdAndStatus(accountId, 1);
    }

    @Test
    @DisplayName("Lấy license DTO theo accountId thành công")
    void getLicenseByAccountId_Success() {
        // Given
        when(licenseKeyRepository.findByAccountIdAndStatus(accountId, 1))
                .thenReturn(Optional.of(licenseKey));

        // When
        LicenseKeyDto result = licenseKeyService.getLicenseByAccountId(accountId);

        // Then
        assertNotNull(result);
        assertEquals(licenseKeyId, result.getId());
        assertEquals(accountId, result.getAccountId());
        verify(licenseKeyRepository, times(1)).findByAccountIdAndStatus(accountId, 1);
    }

    @Test
    @DisplayName("Lấy license DTO thất bại khi không tìm thấy")
    void getLicenseByAccountId_ThrowsException_WhenNotFound() {
        // Given
        when(licenseKeyRepository.findByAccountIdAndStatus(accountId, 1))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> licenseKeyService.getLicenseByAccountId(accountId));
        
        assertEquals("KHÔNG TỒN TẠI", exception.getMessage());
        verify(licenseKeyRepository, times(1)).findByAccountIdAndStatus(accountId, 1);
    }

    @Test
    @DisplayName("Vô hiệu hóa license thành công")
    void deactivateLicense_Success() {
        // Given
        when(licenseKeyRepository.findByKeyAndStatus(licenseKeyString, 1))
                .thenReturn(Optional.of(licenseKey));
        when(licenseKeyRepository.save(any(LicenseKey.class))).thenReturn(licenseKey);

        // When
        licenseKeyService.deactivateLicense(licenseKeyString);

        // Then
        verify(licenseKeyRepository, times(1)).findByKeyAndStatus(licenseKeyString, 1);
        verify(licenseKeyRepository, times(1)).save(licenseKey);
        assertEquals(LicenseKeyEnum.INACTIVE.getCode(), licenseKey.getStatus());
    }

    @Test
    @DisplayName("Vô hiệu hóa license không làm gì khi không tìm thấy")
    void deactivateLicense_DoesNothing_WhenNotFound() {
        // Given
        when(licenseKeyRepository.findByKeyAndStatus(licenseKeyString, 1))
                .thenReturn(Optional.empty());

        // When
        licenseKeyService.deactivateLicense(licenseKeyString);

        // Then
        verify(licenseKeyRepository, times(1)).findByKeyAndStatus(licenseKeyString, 1);
        verify(licenseKeyRepository, never()).save(any(LicenseKey.class));
    }

    @Test
    @DisplayName("Kích hoạt license thành công")
    void activateLicense_Success() {
        // Given
        licenseKey.setStatus(LicenseKeyEnum.INACTIVE.getCode());
        when(licenseKeyRepository.findById(licenseKeyId)).thenReturn(Optional.of(licenseKey));
        when(licenseKeyRepository.save(any(LicenseKey.class))).thenReturn(licenseKey);

        // When
        licenseKeyService.activateLicense(licenseKeyId);

        // Then
        verify(licenseKeyRepository, times(1)).findById(licenseKeyId);
        verify(licenseKeyRepository, times(1)).save(licenseKey);
        assertEquals(LicenseKeyEnum.ACTIVE.getCode(), licenseKey.getStatus());
    }

    @Test
    @DisplayName("Lấy license info thành công khi có license")
    void getLicenseInfoByAccountId_Success_WhenHasLicense() {
        // Given
        when(licenseKeyRepository.findByAccountIdAndStatus(accountId, 1))
                .thenReturn(Optional.of(licenseKey));

        // When
        LicenseKeyInfo result = licenseKeyService.getLicenseInfoByAccountId(accountId);

        // Then
        assertNotNull(result);
        assertTrue(result.getHasPurchased());
        assertNotNull(result.getPurchaseDate());
        verify(licenseKeyRepository, times(1)).findByAccountIdAndStatus(accountId, 1);
    }

    @Test
    @DisplayName("Lấy license info trả về chưa mua khi không có license")
    void getLicenseInfoByAccountId_ReturnsNotPurchased_WhenNoLicense() {
        // Given
        when(licenseKeyRepository.findByAccountIdAndStatus(accountId, 1))
                .thenReturn(Optional.empty());

        // When
        LicenseKeyInfo result = licenseKeyService.getLicenseInfoByAccountId(accountId);

        // Then
        assertNotNull(result);
        assertFalse(result.getHasPurchased());
        assertNull(result.getPurchaseDate());
        verify(licenseKeyRepository, times(1)).findByAccountIdAndStatus(accountId, 1);
    }

    // Helper method to create a test JWT token
    private String createTestJwtToken(String accountId) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payload = "{\"id\":\"" + accountId + "\",\"exp\":9999999999}";
            
            String headerEncoded = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(header.getBytes());
            String payloadEncoded = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes());
            
            return headerEncoded + "." + payloadEncoded + ".signature";
        } catch (Exception e) {
            return null;
        }
    }
}

