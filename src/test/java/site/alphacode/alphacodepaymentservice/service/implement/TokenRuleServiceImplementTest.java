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
import site.alphacode.alphacodepaymentservice.dto.request.create.CreateTokenRule;
import site.alphacode.alphacodepaymentservice.dto.request.patch.PatchTokenRule;
import site.alphacode.alphacodepaymentservice.dto.request.update.UpdateTokenRule;
import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.response.TokenRuleDto;
import site.alphacode.alphacodepaymentservice.entity.TokenRule;
import site.alphacode.alphacodepaymentservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodepaymentservice.repository.TokenRuleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenRuleServiceImplement Tests")
class TokenRuleServiceImplementTest {

    @Mock
    private TokenRuleRepository tokenRuleRepository;

    @InjectMocks
    private TokenRuleServiceImplement tokenRuleService;

    private TokenRule tokenRule;
    private UUID tokenRuleId;
    private CreateTokenRule createTokenRule;
    private UpdateTokenRule updateTokenRule;
    private PatchTokenRule patchTokenRule;

    @BeforeEach
    void setUp() {
        tokenRuleId = UUID.randomUUID();

        tokenRule = TokenRule.builder()
                .id(tokenRuleId)
                .code("TEST_CODE")
                .cost(100)
                .note("Test Note")
                .status(1)
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        createTokenRule = new CreateTokenRule();
        createTokenRule.setCode("NEW_CODE");
        createTokenRule.setCost(200);
        createTokenRule.setNote("New Note");

        updateTokenRule = new UpdateTokenRule();
        updateTokenRule.setId(tokenRuleId);
        updateTokenRule.setCode("UPDATED_CODE");
        updateTokenRule.setCost(300);
        updateTokenRule.setNote("Updated Note");
        updateTokenRule.setStatus(1);

        patchTokenRule = new PatchTokenRule();
        patchTokenRule.setCode("PATCHED_CODE");
        patchTokenRule.setCost(400);
    }

    @Test
    @DisplayName("Tạo token rule thành công")
    void createTokenRule_Success() {
        // Given
        when(tokenRuleRepository.existsByCodeAndStatus(createTokenRule.getCode(), 1)).thenReturn(false);
        when(tokenRuleRepository.save(any(TokenRule.class))).thenAnswer(invocation -> {
            TokenRule tr = invocation.getArgument(0);
            tr.setId(tokenRuleId);
            tr.setCreatedDate(LocalDateTime.now());
            return tr;
        });

        // When
        TokenRuleDto result = tokenRuleService.createTokenRule(createTokenRule);

        // Then
        assertNotNull(result);
        assertEquals(createTokenRule.getCode(), result.getCode());
        assertEquals(createTokenRule.getCost(), result.getCost());
        assertEquals(1, result.getStatus());
        
        verify(tokenRuleRepository, times(1)).existsByCodeAndStatus(createTokenRule.getCode(), 1);
        verify(tokenRuleRepository, times(1)).save(any(TokenRule.class));
    }

    @Test
    @DisplayName("Tạo token rule thất bại khi code đã tồn tại")
    void createTokenRule_ThrowsException_WhenCodeExists() {
        // Given
        when(tokenRuleRepository.existsByCodeAndStatus(createTokenRule.getCode(), 1)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> tokenRuleService.createTokenRule(createTokenRule));
        
        assertTrue(exception.getMessage().contains("đã tồn tại"));
        
        verify(tokenRuleRepository, times(1)).existsByCodeAndStatus(createTokenRule.getCode(), 1);
        verify(tokenRuleRepository, never()).save(any(TokenRule.class));
    }

    @Test
    @DisplayName("Cập nhật token rule thành công")
    void updateTokenRule_Success() {
        // Given
        when(tokenRuleRepository.findById(tokenRuleId)).thenReturn(Optional.of(tokenRule));
        when(tokenRuleRepository.existsByCodeAndIdNot(updateTokenRule.getCode(), tokenRuleId)).thenReturn(false);
        when(tokenRuleRepository.save(any(TokenRule.class))).thenReturn(tokenRule);

        // When
        TokenRuleDto result = tokenRuleService.updateTokenRule(tokenRuleId, updateTokenRule);

        // Then
        assertNotNull(result);
        verify(tokenRuleRepository, times(1)).findById(tokenRuleId);
        verify(tokenRuleRepository, times(1)).existsByCodeAndIdNot(updateTokenRule.getCode(), tokenRuleId);
        verify(tokenRuleRepository, times(1)).save(any(TokenRule.class));
    }

    @Test
    @DisplayName("Cập nhật token rule thất bại khi không tìm thấy")
    void updateTokenRule_ThrowsException_WhenNotFound() {
        // Given
        when(tokenRuleRepository.findById(tokenRuleId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> tokenRuleService.updateTokenRule(tokenRuleId, updateTokenRule));
        
        assertEquals("Token rule không tồn tại.", exception.getMessage());
        
        verify(tokenRuleRepository, times(1)).findById(tokenRuleId);
        verify(tokenRuleRepository, never()).save(any(TokenRule.class));
    }

    @Test
    @DisplayName("Cập nhật token rule thất bại khi code mới đã tồn tại")
    void updateTokenRule_ThrowsException_WhenNewCodeExists() {
        // Given
        when(tokenRuleRepository.findById(tokenRuleId)).thenReturn(Optional.of(tokenRule));
        when(tokenRuleRepository.existsByCodeAndIdNot(updateTokenRule.getCode(), tokenRuleId)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> tokenRuleService.updateTokenRule(tokenRuleId, updateTokenRule));
        
        assertTrue(exception.getMessage().contains("đã tồn tại"));
        
        verify(tokenRuleRepository, times(1)).findById(tokenRuleId);
        verify(tokenRuleRepository, times(1)).existsByCodeAndIdNot(updateTokenRule.getCode(), tokenRuleId);
        verify(tokenRuleRepository, never()).save(any(TokenRule.class));
    }

    @Test
    @DisplayName("Patch token rule thành công")
    void patch_Success() {
        // Given
        when(tokenRuleRepository.findById(tokenRuleId)).thenReturn(Optional.of(tokenRule));
        when(tokenRuleRepository.existsByCodeAndIdNot(patchTokenRule.getCode(), tokenRuleId)).thenReturn(false);
        when(tokenRuleRepository.save(any(TokenRule.class))).thenReturn(tokenRule);

        // When
        TokenRuleDto result = tokenRuleService.patch(tokenRuleId, patchTokenRule);

        // Then
        assertNotNull(result);
        verify(tokenRuleRepository, times(1)).findById(tokenRuleId);
        verify(tokenRuleRepository, times(1)).existsByCodeAndIdNot(patchTokenRule.getCode(), tokenRuleId);
        verify(tokenRuleRepository, times(1)).save(any(TokenRule.class));
    }

    @Test
    @DisplayName("Patch token rule chỉ cập nhật các trường không null")
    void patch_OnlyUpdatesNonNullFields() {
        // Given
        PatchTokenRule partialPatch = new PatchTokenRule();
        partialPatch.setCost(500);
        // code và note là null

        when(tokenRuleRepository.findById(tokenRuleId)).thenReturn(Optional.of(tokenRule));
        when(tokenRuleRepository.save(any(TokenRule.class))).thenReturn(tokenRule);

        // When
        TokenRuleDto result = tokenRuleService.patch(tokenRuleId, partialPatch);

        // Then
        assertNotNull(result);
        verify(tokenRuleRepository, times(1)).findById(tokenRuleId);
        verify(tokenRuleRepository, never()).existsByCodeAndIdNot(anyString(), any());
        verify(tokenRuleRepository, times(1)).save(any(TokenRule.class));
    }

    @Test
    @DisplayName("Lấy token rule theo ID thành công")
    void getTokenRuleById_Success() {
        // Given
        when(tokenRuleRepository.findByIdAndStatus(tokenRuleId, 1)).thenReturn(Optional.of(tokenRule));

        // When
        TokenRuleDto result = tokenRuleService.getTokenRuleById(tokenRuleId);

        // Then
        assertNotNull(result);
        assertEquals(tokenRuleId, result.getId());
        assertEquals("TEST_CODE", result.getCode());
        verify(tokenRuleRepository, times(1)).findByIdAndStatus(tokenRuleId, 1);
    }

    @Test
    @DisplayName("Lấy token rule thất bại khi không tìm thấy")
    void getTokenRuleById_ThrowsException_WhenNotFound() {
        // Given
        when(tokenRuleRepository.findByIdAndStatus(tokenRuleId, 1)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> tokenRuleService.getTokenRuleById(tokenRuleId));
        
        assertEquals("Token rule không tồn tại.", exception.getMessage());
        
        verify(tokenRuleRepository, times(1)).findByIdAndStatus(tokenRuleId, 1);
    }

    @Test
    @DisplayName("Xóa token rule thành công (soft delete)")
    void deleteTokenRule_Success() {
        // Given
        when(tokenRuleRepository.findById(tokenRuleId)).thenReturn(Optional.of(tokenRule));
        when(tokenRuleRepository.save(any(TokenRule.class))).thenReturn(tokenRule);

        // When
        tokenRuleService.deleteTokenRule(tokenRuleId);

        // Then
        verify(tokenRuleRepository, times(1)).findById(tokenRuleId);
        verify(tokenRuleRepository, times(1)).save(any(TokenRule.class));
        assertEquals(0, tokenRule.getStatus());
    }
}

