package site.alphacode.alphacodepaymentservice.service.implement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.alphacode.alphacodepaymentservice.dto.response.UserSubscriptionDashboard;
import site.alphacode.alphacodepaymentservice.entity.Subscription;
import site.alphacode.alphacodepaymentservice.entity.SubscriptionPlan;
import site.alphacode.alphacodepaymentservice.repository.SubscriptionPlanRepository;
import site.alphacode.alphacodepaymentservice.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubsriptionServiceImplement Tests")
class SubsriptionServiceImplementTest {

    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubsriptionServiceImplement subscriptionService;

    private SubscriptionPlan subscriptionPlan;
    private Subscription subscription;
    private UUID accountId;
    private UUID planId;
    private UUID subscriptionId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        planId = UUID.randomUUID();
        subscriptionId = UUID.randomUUID();

        subscriptionPlan = SubscriptionPlan.builder()
                .id(planId)
                .name("Test Plan")
                .price(100000)
                .billingCycle(1)
                .isRecommended(false)
                .status(1)
                .build();

        subscription = Subscription.builder()
                .id(subscriptionId)
                .accountId(accountId)
                .planId(planId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .status(1)
                .build();
    }

    @Test
    @DisplayName("Tạo subscription mới thành công khi chưa có subscription")
    void createOrUpdateSubscription_CreatesNew_WhenNoExistingSubscription() {
        // Given
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionRepository.findByAccountIdAndStatus(accountId, 1)).thenReturn(new ArrayList<>());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> {
            Subscription s = invocation.getArgument(0);
            s.setId(subscriptionId);
            return s;
        });

        // When
        subscriptionService.createOrUpdateSubscription(accountId, planId);

        // Then
        verify(subscriptionPlanRepository, times(1)).findById(planId);
        verify(subscriptionRepository, times(1)).findByAccountIdAndStatus(accountId, 1);
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    @DisplayName("Gia hạn subscription khi cùng plan và có billing cycle")
    void createOrUpdateSubscription_Extends_WhenSamePlanWithBillingCycle() {
        // Given
        LocalDateTime originalEndDate = LocalDateTime.now().plusMonths(1);
        subscription.setEndDate(originalEndDate);
        List<Subscription> existingSubs = List.of(subscription);
        
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionRepository.findByAccountIdAndStatus(accountId, 1)).thenReturn(existingSubs);
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        // When
        subscriptionService.createOrUpdateSubscription(accountId, planId);

        // Then
        verify(subscriptionRepository, times(1)).findByAccountIdAndStatus(accountId, 1);
        verify(subscriptionRepository, times(1)).save(subscription);
        assertTrue(subscription.getEndDate().isAfter(originalEndDate));
    }

    @Test
    @DisplayName("Expire subscription cũ và tạo mới khi plan khác")
    void createOrUpdateSubscription_ExpiresOldAndCreatesNew_WhenDifferentPlan() {
        // Given
        UUID oldPlanId = UUID.randomUUID();
        SubscriptionPlan oldPlan = SubscriptionPlan.builder()
                .id(oldPlanId)
                .billingCycle(1)
                .build();
        subscription.setPlanId(oldPlanId);
        List<Subscription> existingSubs = List.of(subscription);
        
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionRepository.findByAccountIdAndStatus(accountId, 1)).thenReturn(existingSubs);
        when(subscriptionPlanRepository.findById(oldPlanId)).thenReturn(Optional.of(oldPlan));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> {
            Subscription s = invocation.getArgument(0);
            if (s.getId() == null) {
                s.setId(subscriptionId);
            }
            return s;
        });

        // When
        subscriptionService.createOrUpdateSubscription(accountId, planId);

        // Then
        verify(subscriptionRepository, times(1)).findByAccountIdAndStatus(accountId, 1);
        verify(subscriptionRepository, atLeast(2)).save(any(Subscription.class));
        assertEquals(2, subscription.getStatus()); // expired
    }

    @Test
    @DisplayName("Lấy user subscription dashboard thành công khi có subscription")
    void getUserSubscriptionDashboard_Success_WhenHasSubscription() {
        // Given
        when(subscriptionRepository.findFirstByAccountIdAndStatusOrderByStartDateDesc(accountId, 1))
                .thenReturn(Optional.of(subscription));
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));

        // When
        UserSubscriptionDashboard result = subscriptionService.getUserSubscriptionDashboard(accountId);

        // Then
        assertNotNull(result);
        assertEquals("Test Plan", result.getPlanName());
        assertNotNull(result.getEndDate());
        assertEquals(1, result.getStatus());
        verify(subscriptionRepository, times(1))
                .findFirstByAccountIdAndStatusOrderByStartDateDesc(accountId, 1);
        verify(subscriptionPlanRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("Lấy user subscription dashboard trả về no active plan khi không có subscription")
    void getUserSubscriptionDashboard_ReturnsNoActivePlan_WhenNoSubscription() {
        // Given
        when(subscriptionRepository.findFirstByAccountIdAndStatusOrderByStartDateDesc(accountId, 1))
                .thenReturn(Optional.empty());

        // When
        UserSubscriptionDashboard result = subscriptionService.getUserSubscriptionDashboard(accountId);

        // Then
        assertNotNull(result);
        assertEquals("No active plan", result.getPlanName());
        assertNull(result.getEndDate());
        assertEquals(0, result.getStatus());
        verify(subscriptionRepository, times(1))
                .findFirstByAccountIdAndStatusOrderByStartDateDesc(accountId, 1);
        verify(subscriptionPlanRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Tạo subscription với billing cycle = 0 (unlimited)")
    void createOrUpdateSubscription_CreatesUnlimited_WhenBillingCycleIsZero() {
        // Given
        subscriptionPlan.setBillingCycle(0);
        when(subscriptionPlanRepository.findById(planId)).thenReturn(Optional.of(subscriptionPlan));
        when(subscriptionRepository.findByAccountIdAndStatus(accountId, 1)).thenReturn(new ArrayList<>());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> {
            Subscription s = invocation.getArgument(0);
            s.setId(subscriptionId);
            return s;
        });

        // When
        subscriptionService.createOrUpdateSubscription(accountId, planId);

        // Then
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }
}

