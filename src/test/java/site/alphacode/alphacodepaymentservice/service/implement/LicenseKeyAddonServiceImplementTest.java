package site.alphacode.alphacodepaymentservice.service.implement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.alphacode.alphacodepaymentservice.dto.request.ValidateAddonRequest;
import site.alphacode.alphacodepaymentservice.dto.request.create.CreateLincenseKeyAddon;
import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyAddonDto;
import site.alphacode.alphacodepaymentservice.entity.LicenseKey;
import site.alphacode.alphacodepaymentservice.entity.LicenseKeyAddon;
import site.alphacode.alphacodepaymentservice.repository.LicenseKeyAddonRepository;
import site.alphacode.alphacodepaymentservice.repository.LicenseKeyRepository;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyService;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LicenseKeyAddonServiceImplement Tests")
class LicenseKeyAddonServiceImplementTest {

    @Mock
    private LicenseKeyAddonRepository licenseKeyAddonRepository;

    @Mock
    private LicenseKeyRepository licenseKeyRepository;

    @Mock
    private LicenseKeyService licenseKeyService;

    @InjectMocks
    private LicenseKeyAddonServiceImplement licenseKeyAddonService;

    private LicenseKeyAddon licenseKeyAddon;
    private LicenseKey licenseKey;
    private UUID licenseKeyAddonId;
    private UUID addonId;
    private UUID licenseKeyId;
    private UUID accountId;
    private String licenseKeyString;
    private CreateLincenseKeyAddon createLincenseKeyAddon;

    @BeforeEach
    void setUp() {
        licenseKeyAddonId = UUID.randomUUID();
        addonId = UUID.randomUUID();
        licenseKeyId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        licenseKeyString = "testkey12345678";

        licenseKey = LicenseKey.builder()
                .id(licenseKeyId)
                .key(licenseKeyString)
                .accountId(accountId)
                .status(1)
                .build();

        licenseKeyAddon = LicenseKeyAddon.builder()
                .id(licenseKeyAddonId)
                .addonId(addonId)
                .licenseKeyId(licenseKeyId)
                .status(1)
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        createLincenseKeyAddon = new CreateLincenseKeyAddon();
        createLincenseKeyAddon.setAddonId(addonId);
        createLincenseKeyAddon.setLicenseKeyId(licenseKeyId);
        createLincenseKeyAddon.setStatus(1);
    }

    @Test
    @DisplayName("Tạo license key addon thành công")
    void create_Success() {
        // Given
        when(licenseKeyAddonRepository.save(any(LicenseKeyAddon.class))).thenAnswer(invocation -> {
            LicenseKeyAddon lka = invocation.getArgument(0);
            lka.setId(licenseKeyAddonId);
            lka.setCreatedDate(LocalDateTime.now());
            return lka;
        });

        // When
        LicenseKeyAddonDto result = licenseKeyAddonService.create(createLincenseKeyAddon);

        // Then
        assertNotNull(result);
        assertEquals(addonId, result.getAddonId());
        assertEquals(licenseKeyId, result.getLicenseKeyId());
        verify(licenseKeyAddonRepository, times(1)).save(any(LicenseKeyAddon.class));
    }

    @Test
    @DisplayName("Lấy license key addon theo ID thành công")
    void getById_Success() {
        // Given
        when(licenseKeyAddonRepository.findById(licenseKeyAddonId))
                .thenReturn(Optional.of(licenseKeyAddon));

        // When
        LicenseKeyAddonDto result = licenseKeyAddonService.getById(licenseKeyAddonId);

        // Then
        assertNotNull(result);
        assertEquals(licenseKeyAddonId, result.getId());
        verify(licenseKeyAddonRepository, times(1)).findById(licenseKeyAddonId);
    }

    @Test
    @DisplayName("Lấy license key addon thất bại khi không tìm thấy")
    void getById_ThrowsException_WhenNotFound() {
        // Given
        when(licenseKeyAddonRepository.findById(licenseKeyAddonId))
                .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> licenseKeyAddonService.getById(licenseKeyAddonId));
        
        assertTrue(exception.getMessage().contains("Không tìm thấy"));
        
        verify(licenseKeyAddonRepository, times(1)).findById(licenseKeyAddonId);
    }

    @Test
    @DisplayName("Kiểm tra addon active cho license key thành công")
    void isActiveAddonForLicenseKey_Success_WhenActive() {
        // Given
        when(licenseKeyRepository.findLicenseKeyByKey(licenseKeyString))
                .thenReturn(Optional.of(licenseKey));
        when(licenseKeyAddonRepository.findByAddonIdAndLicenseKeyIdAndStatus(addonId, licenseKeyId, 1))
                .thenReturn(Optional.of(licenseKeyAddon));

        // When
        boolean result = licenseKeyAddonService.isActiveAddonForLicenseKey(addonId, licenseKeyString);

        // Then
        assertTrue(result);
        verify(licenseKeyRepository, times(1)).findLicenseKeyByKey(licenseKeyString);
        verify(licenseKeyAddonRepository, times(1))
                .findByAddonIdAndLicenseKeyIdAndStatus(addonId, licenseKeyId, 1);
    }

    @Test
    @DisplayName("Kiểm tra addon active trả về false khi không active")
    void isActiveAddonForLicenseKey_False_WhenNotActive() {
        // Given
        when(licenseKeyRepository.findLicenseKeyByKey(licenseKeyString))
                .thenReturn(Optional.of(licenseKey));
        when(licenseKeyAddonRepository.findByAddonIdAndLicenseKeyIdAndStatus(addonId, licenseKeyId, 1))
                .thenReturn(Optional.empty());

        // When
        boolean result = licenseKeyAddonService.isActiveAddonForLicenseKey(addonId, licenseKeyString);

        // Then
        assertFalse(result);
        verify(licenseKeyRepository, times(1)).findLicenseKeyByKey(licenseKeyString);
        verify(licenseKeyAddonRepository, times(1))
                .findByAddonIdAndLicenseKeyIdAndStatus(addonId, licenseKeyId, 1);
    }

    @Test
    @DisplayName("Kích hoạt license key addon thành công")
    void activate_Success() {
        // Given
        licenseKeyAddon.setStatus(2); // inactive
        when(licenseKeyAddonRepository.findById(licenseKeyAddonId))
                .thenReturn(Optional.of(licenseKeyAddon));
        when(licenseKeyAddonRepository.save(any(LicenseKeyAddon.class)))
                .thenReturn(licenseKeyAddon);

        // When
        boolean result = licenseKeyAddonService.activate(licenseKeyAddonId);

        // Then
        assertTrue(result);
        assertEquals(1, licenseKeyAddon.getStatus());
        verify(licenseKeyAddonRepository, times(1)).findById(licenseKeyAddonId);
        verify(licenseKeyAddonRepository, times(1)).save(licenseKeyAddon);
    }

    @Test
    @DisplayName("Kiểm tra addon active theo category thành công")
    void isActiveAddonForLicenseKey_ByCategory_Success() {
        // Given
        Integer category = 1;
        when(licenseKeyAddonRepository.findActiveAddonByCategory(category, licenseKeyString, 1))
                .thenReturn(Optional.of(licenseKeyAddon));

        // When
        boolean result = licenseKeyAddonService.isActiveAddonForLicenseKey(category, licenseKeyString);

        // Then
        assertTrue(result);
        verify(licenseKeyAddonRepository, times(1))
                .findActiveAddonByCategory(category, licenseKeyString, 1);
    }

    @Test
    @DisplayName("Validate addon thành công")
    void validateAddon_Success() {
        // Given
        ValidateAddonRequest request = new ValidateAddonRequest();
        request.setKey(licenseKeyString);
        request.setAccountId(accountId);
        request.setCategory(1);

        when(licenseKeyService.validateLicense(licenseKeyString, accountId)).thenReturn(true);
        when(licenseKeyAddonRepository.findActiveAddonByCategory(1, licenseKeyString, 1))
                .thenReturn(Optional.of(licenseKeyAddon));

        // When
        boolean result = licenseKeyAddonService.validateAddon(request);

        // Then
        assertTrue(result);
        verify(licenseKeyService, times(1)).validateLicense(licenseKeyString, accountId);
        verify(licenseKeyAddonRepository, times(1))
                .findActiveAddonByCategory(1, licenseKeyString, 1);
    }

    @Test
    @DisplayName("Validate addon thất bại khi license key không hợp lệ")
    void validateAddon_False_WhenLicenseKeyInvalid() {
        // Given
        ValidateAddonRequest request = new ValidateAddonRequest();
        request.setKey(licenseKeyString);
        request.setAccountId(accountId);
        request.setCategory(1);

        when(licenseKeyService.validateLicense(licenseKeyString, accountId)).thenReturn(false);

        // When
        boolean result = licenseKeyAddonService.validateAddon(request);

        // Then
        assertFalse(result);
        verify(licenseKeyService, times(1)).validateLicense(licenseKeyString, accountId);
        verify(licenseKeyAddonRepository, never())
                .findActiveAddonByCategory(any(), any(), any());
    }

    @Test
    @DisplayName("Validate addon với access token thành công")
    void validateAddon_WithAccessToken_Success() {
        // Given
        String token = createTestJwtToken(accountId.toString());
        when(licenseKeyService.validateLicense(licenseKeyString, accountId)).thenReturn(true);
        when(licenseKeyAddonRepository.findActiveAddonByCategory(1, licenseKeyString, 1))
                .thenReturn(Optional.of(licenseKeyAddon));

        // When
        boolean result = licenseKeyAddonService.validateAddon(token, licenseKeyString, accountId, 1);

        // Then
        assertTrue(result);
        verify(licenseKeyService, times(1)).validateLicense(licenseKeyString, accountId);
    }

    @Test
    @DisplayName("Validate addon thất bại khi tham số null")
    void validateAddon_False_WhenParametersNull() {
        // When
        boolean result = licenseKeyAddonService.validateAddon(null, null, null, null);

        // Then
        assertFalse(result);
        verify(licenseKeyService, never()).validateLicense(any(), any());
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

