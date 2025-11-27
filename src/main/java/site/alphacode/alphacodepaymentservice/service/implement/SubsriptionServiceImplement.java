package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.UserSubscriptionDashboard;
import site.alphacode.alphacodepaymentservice.entity.Subscription;
import site.alphacode.alphacodepaymentservice.entity.SubscriptionPlan;
import site.alphacode.alphacodepaymentservice.mapper.SubscriptionMapper;
import site.alphacode.alphacodepaymentservice.repository.SubscriptionPlanRepository;
import site.alphacode.alphacodepaymentservice.repository.SubscriptionRepository;
import site.alphacode.alphacodepaymentservice.service.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubsriptionServiceImplement implements SubscriptionService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user_subscription_dashboard", allEntries = true),
            @CacheEvict(value = "quota", key = "#accountId")
    })
    public void createOrUpdateSubscription(UUID accountId, UUID planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        List<Subscription> currentSubs = subscriptionRepository
                .findByAccountIdAndStatus(accountId, 1);

        boolean extended = false;

        for (Subscription sub : currentSubs) {
            SubscriptionPlan oldPlan = subscriptionPlanRepository.findById(sub.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Old plan not found"));

            // Cùng monthly/unlimited → gia hạn
            if (oldPlan.getId().equals(plan.getId()) && plan.getBillingCycle() > 0) {
                sub.setEndDate(sub.getEndDate().plusMonths(plan.getBillingCycle()));
                subscriptionRepository.save(sub);
                extended = true;
            }
            // Khác plan hoặc quota-limited → expire cũ
            else {
                sub.setStatus(2); // expired
                subscriptionRepository.save(sub);
            }
        }

        // Nếu đã gia hạn → không cần tạo mới
        if (extended) return;

        // Tạo subscription mới
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = plan.getBillingCycle() > 0 ? now.plusMonths(plan.getBillingCycle()) : null;

        Subscription newSub = Subscription.builder()
                .accountId(accountId)
                .planId(plan.getId())
                .startDate(now)
                .endDate(endDate)
                .status(1)
                .build();

        subscriptionRepository.save(newSub);
    }

    @Override
    @Cacheable(value = "user_subscription_dashboard", key = "#accountId")
    public UserSubscriptionDashboard getUserSubscriptionDashboard(UUID accountId) {
        // Tìm subscription đang hoạt động (status = 1)
        Subscription activeSub = subscriptionRepository.findFirstByAccountIdAndStatusOrderByStartDateDesc(accountId, 1)
                .orElse(null);

        if (activeSub == null) {
            // Không có gói nào đang hoạt động
            UserSubscriptionDashboard dashboard = new UserSubscriptionDashboard();
            dashboard.setPlanName("No active plan");
            dashboard.setEndDate(null);
            dashboard.setStatus(0);
            return dashboard;
        }

        SubscriptionPlan plan = subscriptionPlanRepository.findById(activeSub.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // Map sang DTO
        UserSubscriptionDashboard dashboard = new UserSubscriptionDashboard();
        dashboard.setPlanName(plan.getName());
        dashboard.setEndDate(activeSub.getEndDate());
        dashboard.setStatus(activeSub.getStatus());

        return dashboard;
    }

}