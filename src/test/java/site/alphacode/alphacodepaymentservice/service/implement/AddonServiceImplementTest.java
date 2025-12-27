package site.alphacode.alphacodepaymentservice.service.implement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import site.alphacode.alphacodepaymentservice.dto.request.create.CreateAddon;
import site.alphacode.alphacodepaymentservice.dto.request.patch.PatchAddon;
import site.alphacode.alphacodepaymentservice.dto.request.update.UpdateAddon;
import site.alphacode.alphacodepaymentservice.dto.response.AddonDto;
import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.entity.Addon;
import site.alphacode.alphacodepaymentservice.exception.ConflictException;
import site.alphacode.alphacodepaymentservice.repository.AddonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddonServiceImplement Tests")
class AddonServiceImplementTest {

    @Mock
    private AddonRepository addonRepository;

    @InjectMocks
    private AddonServiceImplement addonService;

    private Addon addon;
    private UUID addonId;
    private CreateAddon createAddon;
    private UpdateAddon updateAddon;
    private PatchAddon patchAddon;

    @BeforeEach
    void setUp() {
        addonId = UUID.randomUUID();
        
        addon = Addon.builder()
                .id(addonId)
                .name("Test Addon")
                .price(100000)
                .category(1)
                .description("Test Description")
                .status(1)
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        createAddon = new CreateAddon();
        createAddon.setName("New Addon");
        createAddon.setPrice(150000);
        createAddon.setCategory(2);
        createAddon.setDescription("New Description");

        updateAddon = UpdateAddon.builder()
                .id(addonId)
                .name("Updated Addon")
                .price(200000)
                .category(1)
                .description("Updated Description")
                .status(1)
                .build();

        patchAddon = PatchAddon.builder()
                .name("Patched Addon")
                .price(250000)
                .build();
    }

    @Test
    @DisplayName("Tạo addon thành công")
    void create_Success() {
        // Given
        when(addonRepository.existsAddonByName(createAddon.getName())).thenReturn(false);
        when(addonRepository.existsAddonByCategory(createAddon.getCategory())).thenReturn(false);
        when(addonRepository.save(any(Addon.class))).thenAnswer(invocation -> {
            Addon a = invocation.getArgument(0);
            a.setId(addonId);
            a.setCreatedDate(LocalDateTime.now());
            return a;
        });

        // When
        AddonDto result = addonService.create(createAddon);

        // Then
        assertNotNull(result);
        assertEquals(createAddon.getName(), result.getName());
        assertEquals(createAddon.getPrice(), result.getPrice());
        assertEquals(createAddon.getCategory(), result.getCategory());
        assertEquals(1, result.getStatus());
        
        verify(addonRepository, times(1)).existsAddonByName(createAddon.getName());
        verify(addonRepository, times(1)).existsAddonByCategory(createAddon.getCategory());
        verify(addonRepository, times(1)).save(any(Addon.class));
    }

    @Test
    @DisplayName("Tạo addon thất bại khi tên đã tồn tại")
    void create_ThrowsException_WhenNameExists() {
        // Given
        when(addonRepository.existsAddonByName(createAddon.getName())).thenReturn(true);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, 
            () -> addonService.create(createAddon));
        
        assertEquals("Tên addon đã tồn tại", exception.getMessage());
        
        verify(addonRepository, times(1)).existsAddonByName(createAddon.getName());
        verify(addonRepository, never()).existsAddonByCategory(any());
        verify(addonRepository, never()).save(any(Addon.class));
    }

    @Test
    @DisplayName("Tạo addon thất bại khi category đã tồn tại")
    void create_ThrowsException_WhenCategoryExists() {
        // Given
        when(addonRepository.existsAddonByName(createAddon.getName())).thenReturn(false);
        when(addonRepository.existsAddonByCategory(createAddon.getCategory())).thenReturn(true);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, 
            () -> addonService.create(createAddon));
        
        assertEquals("Chỉ được phép có một addon cho mỗi danh mục", exception.getMessage());
        
        verify(addonRepository, times(1)).existsAddonByName(createAddon.getName());
        verify(addonRepository, times(1)).existsAddonByCategory(createAddon.getCategory());
        verify(addonRepository, never()).save(any(Addon.class));
    }

    @Test
    @DisplayName("Cập nhật addon thất bại khi không tìm thấy")
    void update_ThrowsException_WhenNotFound() {
        // Given
        when(addonRepository.findNoneDeletedById(addonId)).thenReturn(Optional.empty());

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, 
            () -> addonService.update(addonId, updateAddon));
        
        assertTrue(exception.getMessage().contains("Không tìm thấy addon"));
        
        verify(addonRepository, times(1)).findNoneDeletedById(addonId);
        verify(addonRepository, never()).save(any(Addon.class));
    }

    @Test
    @DisplayName("Cập nhật addon thất bại khi tên mới đã tồn tại")
    void update_ThrowsException_WhenNewNameExists() {
        // Given
        Addon existingWithName = Addon.builder().id(UUID.randomUUID()).build();
        when(addonRepository.findNoneDeletedById(addonId)).thenReturn(Optional.of(addon));
        when(addonRepository.findNoneDeletedByName(updateAddon.getName())).thenReturn(Optional.of(existingWithName));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, 
            () -> addonService.update(addonId, updateAddon));
        
        assertTrue(exception.getMessage().contains("đã tồn tại"));
        
        verify(addonRepository, times(1)).findNoneDeletedById(addonId);
        verify(addonRepository, never()).save(any(Addon.class));
    }

    @Test
    @DisplayName("Patch addon thành công")
    void patch_Success() {
        // Given
        when(addonRepository.findNoneDeletedById(addonId)).thenReturn(Optional.of(addon));
        when(addonRepository.findNoneDeletedByName(patchAddon.getName())).thenReturn(Optional.empty());
        when(addonRepository.existsAddonByCategory(any())).thenReturn(false);
        when(addonRepository.save(any(Addon.class))).thenReturn(addon);

        // When
        AddonDto result = addonService.patch(addonId, patchAddon);

        // Then
        assertNotNull(result);
        verify(addonRepository, times(1)).findNoneDeletedById(addonId);
        verify(addonRepository, times(1)).save(any(Addon.class));
    }

    @Test
    @DisplayName("Xóa addon thành công")
    void delete_Success() {
        // Given
        when(addonRepository.findNoneDeletedById(addonId)).thenReturn(Optional.of(addon));
        when(addonRepository.save(any(Addon.class))).thenReturn(addon);

        // When
        addonService.delete(addonId);

        // Then
        verify(addonRepository, times(1)).findNoneDeletedById(addonId);
        verify(addonRepository, times(1)).save(any(Addon.class));
        assertEquals(0, addon.getStatus());
    }

    @Test
    @DisplayName("Lấy addon theo ID thành công")
    void getNoneDeleteById_Success() {
        // Given
        when(addonRepository.findNoneDeletedById(addonId)).thenReturn(Optional.of(addon));

        // When
        AddonDto result = addonService.getNoneDeleteById(addonId);

        // Then
        assertNotNull(result);
        assertEquals(addonId, result.getId());
        verify(addonRepository, times(1)).findNoneDeletedById(addonId);
    }

    @Test
    @DisplayName("Lấy addon active theo ID thành công")
    void getActiveById_Success() {
        // Given
        when(addonRepository.findByIdAndStatus(addonId, 1)).thenReturn(Optional.of(addon));

        // When
        AddonDto result = addonService.getActiveById(addonId);

        // Then
        assertNotNull(result);
        assertEquals(addonId, result.getId());
        verify(addonRepository, times(1)).findByIdAndStatus(addonId, 1);
    }
}

