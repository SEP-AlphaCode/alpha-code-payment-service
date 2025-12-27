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
import site.alphacode.alphacodepaymentservice.dto.request.create.CreateSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.dto.request.patch.PatchSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.dto.request.update.UpdateSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.response.SubscriptionPlanDto;
import site.alphacode.alphacodepaymentservice.entity.SubscriptionPlan;
import site.alphacode.alphacodepaymentservice.exception.ConflictException;
import site.alphacode.alphacodepaymentservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodepaymentservice.repository.SubscriptionPlanRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionPlanServiceImplement Tests")
class SubscriptionPlanServiceImplementTest {

    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @InjectMocks
    private SubscriptionPlanServiceImplement subscriptionPlanService;

    private SubscriptionPlan subscriptionPlan;
    private UUID planId;
    private CreateSubscriptionPlan createSubscriptionPlan;
    private UpdateSubscriptionPlan updateSubscriptionPlan;
    private PatchSubscriptionPlan patchSubscriptionPlan;

    @BeforeEach
    void setUp() {
        planId = UUID.randomUUID();

        subscriptionPlan = SubscriptionPlan.builder()
                .id(planId)
                .name("Test Plan")
                .description("Test Description")
                .price(100000)
                .billingCycle(1)
                .isRecommended(false)
                .status(1)
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        createSubscriptionPlan = new CreateSubscriptionPlan();
        createSubscriptionPlan.setName("New Plan");
        createSubscriptionPlan.setDescription("New Description");
        createSubscriptionPlan.setPrice(150000);
        createSubscriptionPlan.setBillingCycle(3);
        createSubscriptionPlan.setIsRecommended(true);

        updateSubscriptionPlan = new UpdateSubscriptionPlan();
        updateSubscriptionPlan.setId(planId);
        updateSubscriptionPlan.setName("Updated Plan");
        updateSubscriptionPlan.setDescription("Updated Description");
        updateSubscriptionPlan.setPrice(200000);
        updateSubscriptionPlan.setBillingCycle(6);
        updateSubscriptionPlan.setIsRecommended(false);

        patchSubscriptionPlan = new PatchSubscriptionPlan();
        patchSubscriptionPlan.setName("Patched Plan");
        patchSubscriptionPlan.setPrice(250000);
    }

    @Test
    @DisplayName("Tạo subscription plan thành công")
    void create_Success() {
        // Given
        when(subscriptionPlanRepository.findByNameAndStatus(createSubscriptionPlan.getName(), 1))
                .thenReturn(null);
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenAnswer(invocation -> {
            SubscriptionPlan sp = invocation.getArgument(0);
            sp.setId(planId);
            sp.setCreatedDate(LocalDateTime.now());
            return sp;
        });

        // When
        SubscriptionPlanDto result = subscriptionPlanService.create(createSubscriptionPlan);

        // Then
        assertNotNull(result);
        assertEquals(createSubscriptionPlan.getName(), result.getName());
        assertEquals(createSubscriptionPlan.getPrice(), result.getPrice());
        assertEquals(1, result.getStatus());
        
        verify(subscriptionPlanRepository, times(1)).findByNameAndStatus(createSubscriptionPlan.getName(), 1);
        verify(subscriptionPlanRepository, times(1)).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Tạo subscription plan thất bại khi tên đã tồn tại")
    void create_ThrowsException_WhenNameExists() {
        // Given
        when(subscriptionPlanRepository.findByNameAndStatus(createSubscriptionPlan.getName(), 1))
                .thenReturn(subscriptionPlan);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, 
            () -> subscriptionPlanService.create(createSubscriptionPlan));
        
        assertTrue(exception.getMessage().contains("đã tồn tại"));
        
        verify(subscriptionPlanRepository, times(1)).findByNameAndStatus(createSubscriptionPlan.getName(), 1);
        verify(subscriptionPlanRepository, never()).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Lấy subscription plan theo ID thành công")
    void getById_Success() {
        // Given
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));

        // When
        SubscriptionPlanDto result = subscriptionPlanService.getById(planId);

        // Then
        assertNotNull(result);
        assertEquals(planId, result.getId());
        assertEquals("Test Plan", result.getName());
        verify(subscriptionPlanRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("Lấy subscription plan thất bại khi không tìm thấy")
    void getById_ThrowsException_WhenNotFound() {
        // Given
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> subscriptionPlanService.getById(planId));
        
        assertTrue(exception.getMessage().contains("không tồn tại"));
        
        verify(subscriptionPlanRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("Cập nhật subscription plan thành công")
    void update_Success() {
        // Given
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionPlanRepository.findByNameAndStatus(updateSubscriptionPlan.getName(), 1))
                .thenReturn(null);
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(subscriptionPlan);

        // When
        SubscriptionPlanDto result = subscriptionPlanService.update(planId, updateSubscriptionPlan);

        // Then
        assertNotNull(result);
        verify(subscriptionPlanRepository, times(1)).findById(planId);
        verify(subscriptionPlanRepository, times(1)).findByNameAndStatus(updateSubscriptionPlan.getName(), 1);
        verify(subscriptionPlanRepository, times(1)).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Cập nhật subscription plan thất bại khi không tìm thấy")
    void update_ThrowsException_WhenNotFound() {
        // Given
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> subscriptionPlanService.update(planId, updateSubscriptionPlan));
        
        assertTrue(exception.getMessage().contains("không tồn tại"));
        
        verify(subscriptionPlanRepository, times(1)).findById(planId);
        verify(subscriptionPlanRepository, never()).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Cập nhật subscription plan thất bại khi tên mới đã tồn tại")
    void update_ThrowsException_WhenNewNameExists() {
        // Given
        SubscriptionPlan existingPlan = SubscriptionPlan.builder().id(UUID.randomUUID()).build();
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionPlanRepository.findByNameAndStatus(updateSubscriptionPlan.getName(), 1))
                .thenReturn(existingPlan);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, 
            () -> subscriptionPlanService.update(planId, updateSubscriptionPlan));
        
        assertTrue(exception.getMessage().contains("đã tồn tại"));
        
        verify(subscriptionPlanRepository, times(1)).findById(planId);
        verify(subscriptionPlanRepository, never()).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Patch subscription plan thành công")
    void patch_Success() {
        // Given
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(subscriptionPlan);

        // When
        SubscriptionPlanDto result = subscriptionPlanService.patch(planId, patchSubscriptionPlan);

        // Then
        assertNotNull(result);
        verify(subscriptionPlanRepository, times(1)).findById(planId);
        verify(subscriptionPlanRepository, times(1)).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Xóa subscription plan thành công (soft delete)")
    void delete_Success() {
        // Given
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(subscriptionPlan);

        // When
        subscriptionPlanService.delete(planId);

        // Then
        verify(subscriptionPlanRepository, times(1)).findById(planId);
        verify(subscriptionPlanRepository, times(1)).save(any(SubscriptionPlan.class));
        assertEquals(0, subscriptionPlan.getStatus());
    }
}

