package site.alphacode.alphacodepaymentservice.service.implement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.alphacode.alphacodepaymentservice.dto.response.KeyPriceDto;
import site.alphacode.alphacodepaymentservice.entity.KeyPrice;
import site.alphacode.alphacodepaymentservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodepaymentservice.repository.KeyPriceRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KeyPriceServiceImplement Tests")
class KeyPriceServiceImplementTest {

    @Mock
    private KeyPriceRepository keyPriceRepository;

    @InjectMocks
    private KeyPriceServiceImplement keyPriceService;

    private KeyPrice keyPrice;
    private UUID keyPriceId;
    private Integer testPrice;

    @BeforeEach
    void setUp() {
        keyPriceId = UUID.randomUUID();
        testPrice = 100000;
        
        keyPrice = KeyPrice.builder()
                .id(keyPriceId)
                .price(testPrice)
                .status(1)
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Tạo key price thành công khi chưa có key price nào")
    void createKeyPrice_Success_WhenNoKeyPriceExists() {
        // Given
        when(keyPriceRepository.count()).thenReturn(0L);
        when(keyPriceRepository.save(any(KeyPrice.class))).thenAnswer(invocation -> {
            KeyPrice kp = invocation.getArgument(0);
            kp.setId(keyPriceId);
            kp.setCreatedDate(LocalDateTime.now());
            return kp;
        });
        doNothing().when(keyPriceRepository).deactivateAllActiveKeys();

        // When
        KeyPriceDto result = keyPriceService.createKeyPrice(testPrice);

        // Then
        assertNotNull(result);
        assertEquals(testPrice, result.getPrice());
        assertEquals(1, result.getStatus());
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedDate());
        
        verify(keyPriceRepository, times(1)).count();
        verify(keyPriceRepository, times(1)).deactivateAllActiveKeys();
        verify(keyPriceRepository, times(1)).save(any(KeyPrice.class));
    }

    @Test
    @DisplayName("Tạo key price thất bại khi đã có key price tồn tại")
    void createKeyPrice_ThrowsException_WhenKeyPriceExists() {
        // Given
        when(keyPriceRepository.count()).thenReturn(1L);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> keyPriceService.createKeyPrice(testPrice));
        
        assertEquals("Key price đã tồn tại. Không thể tạo mới.", exception.getMessage());
        
        verify(keyPriceRepository, times(1)).count();
        verify(keyPriceRepository, never()).deactivateAllActiveKeys();
        verify(keyPriceRepository, never()).save(any(KeyPrice.class));
    }

    @Test
    @DisplayName("Cập nhật key price thành công")
    void updateKeyPrice_Success() {
        // Given
        Integer newPrice = 150000;
        when(keyPriceRepository.findById(keyPriceId)).thenReturn(Optional.of(keyPrice));
        when(keyPriceRepository.save(any(KeyPrice.class))).thenAnswer(invocation -> {
            KeyPrice kp = invocation.getArgument(0);
            kp.setLastUpdated(LocalDateTime.now());
            return kp;
        });

        // When
        KeyPriceDto result = keyPriceService.updateKeyPrice(keyPriceId, newPrice);

        // Then
        assertNotNull(result);
        assertEquals(newPrice, result.getPrice());
        assertEquals(keyPriceId, result.getId());
        assertNotNull(result.getLastUpdated());
        
        verify(keyPriceRepository, times(1)).findById(keyPriceId);
        verify(keyPriceRepository, times(1)).save(any(KeyPrice.class));
    }

    @Test
    @DisplayName("Cập nhật key price thất bại khi không tìm thấy")
    void updateKeyPrice_ThrowsException_WhenNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(keyPriceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> keyPriceService.updateKeyPrice(nonExistentId, testPrice));
        
        assertEquals("Key price không tồn tại.", exception.getMessage());
        
        verify(keyPriceRepository, times(1)).findById(nonExistentId);
        verify(keyPriceRepository, never()).save(any(KeyPrice.class));
    }

    @Test
    @DisplayName("Lấy key price thành công")
    void getKeyPrice_Success() {
        // Given
        when(keyPriceRepository.findTopByOrderByCreatedDateDesc()).thenReturn(Optional.of(keyPrice));

        // When
        KeyPriceDto result = keyPriceService.getKeyPrice();

        // Then
        assertNotNull(result);
        assertEquals(keyPriceId, result.getId());
        assertEquals(testPrice, result.getPrice());
        assertEquals(1, result.getStatus());
        
        verify(keyPriceRepository, times(1)).findTopByOrderByCreatedDateDesc();
    }

    @Test
    @DisplayName("Lấy key price thất bại khi không tìm thấy")
    void getKeyPrice_ThrowsException_WhenNotFound() {
        // Given
        when(keyPriceRepository.findTopByOrderByCreatedDateDesc()).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> keyPriceService.getKeyPrice());
        
        assertEquals("Key price không tồn tại.", exception.getMessage());
        
        verify(keyPriceRepository, times(1)).findTopByOrderByCreatedDateDesc();
    }

    @Test
    @DisplayName("Xóa key price thành công")
    void deleteKeyPrice_Success() {
        // Given
        when(keyPriceRepository.findById(keyPriceId)).thenReturn(Optional.of(keyPrice));
        doNothing().when(keyPriceRepository).delete(any(KeyPrice.class));

        // When
        keyPriceService.deleteKeyPrice(keyPriceId);

        // Then
        verify(keyPriceRepository, times(1)).findById(keyPriceId);
        verify(keyPriceRepository, times(1)).delete(keyPrice);
    }

    @Test
    @DisplayName("Xóa key price thất bại khi không tìm thấy")
    void deleteKeyPrice_ThrowsException_WhenNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(keyPriceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> keyPriceService.deleteKeyPrice(nonExistentId));
        
        assertEquals("Key price không tồn tại.", exception.getMessage());
        
        verify(keyPriceRepository, times(1)).findById(nonExistentId);
        verify(keyPriceRepository, never()).delete(any(KeyPrice.class));
    }

    @Test
    @DisplayName("Tạo key price với giá trị null")
    void createKeyPrice_WithNullPrice() {
        // Given
        when(keyPriceRepository.count()).thenReturn(0L);
        when(keyPriceRepository.save(any(KeyPrice.class))).thenAnswer(invocation -> {
            KeyPrice kp = invocation.getArgument(0);
            kp.setId(keyPriceId);
            kp.setCreatedDate(LocalDateTime.now());
            return kp;
        });
        doNothing().when(keyPriceRepository).deactivateAllActiveKeys();

        // When
        KeyPriceDto result = keyPriceService.createKeyPrice(null);

        // Then
        assertNotNull(result);
        assertNull(result.getPrice());
        
        verify(keyPriceRepository, times(1)).count();
        verify(keyPriceRepository, times(1)).save(any(KeyPrice.class));
    }

    @Test
    @DisplayName("Cập nhật key price với giá trị 0")
    void updateKeyPrice_WithZeroPrice() {
        // Given
        Integer zeroPrice = 0;
        when(keyPriceRepository.findById(keyPriceId)).thenReturn(Optional.of(keyPrice));
        when(keyPriceRepository.save(any(KeyPrice.class))).thenAnswer(invocation -> {
            KeyPrice kp = invocation.getArgument(0);
            kp.setLastUpdated(LocalDateTime.now());
            return kp;
        });

        // When
        KeyPriceDto result = keyPriceService.updateKeyPrice(keyPriceId, zeroPrice);

        // Then
        assertNotNull(result);
        assertEquals(zeroPrice, result.getPrice());
        
        verify(keyPriceRepository, times(1)).findById(keyPriceId);
        verify(keyPriceRepository, times(1)).save(any(KeyPrice.class));
    }
}

