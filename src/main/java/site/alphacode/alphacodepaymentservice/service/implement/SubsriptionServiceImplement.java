package site.alphacode.alphacodepaymentservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    public void createOrUpdateSubscription(UUID accountId, UUID planId) {
        // Lấy plan mới
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // Lấy tất cả subscription ACTIVE hiện tại của user
        List<Subscription> currentSubs = subscriptionRepository
                .findByAccountIdAndStatus(accountId, 1); // 1 = ACTIVE

        // Biến dùng lưu quota dư nếu có
        int leftoverQuota = 0;
        boolean hasSameMonthly = false;

        for (Subscription sub : currentSubs) {
            SubscriptionPlan oldPlan = subscriptionPlanRepository.findById(sub.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Old plan not found"));

            // Nếu cả cũ & mới đều quota-limited, cộng quota
            if (oldPlan.getBillingCycle() == 0 && plan.getBillingCycle() == 0
                    && oldPlan.getId().equals(plan.getId())) {
                leftoverQuota += sub.getRemainingQuota();
                sub.setStatus(2); // expired cũ
                subscriptionRepository.save(sub);
            }
            // Nếu cả cũ & mới đều monthly/unlimited và cùng loại, gia hạn
            else if (oldPlan.getBillingCycle() > 0 && plan.getBillingCycle() > 0
                    && oldPlan.getId().equals(plan.getId())) {
                // Gia hạn endDate
                sub.setEndDate(sub.getEndDate().plusMonths(plan.getBillingCycle()));
                subscriptionRepository.save(sub);
                hasSameMonthly = true;
            }
            // Nếu khác loại hoặc nâng cấp quota → monthly, expire cũ
            else if (!(oldPlan.getId().equals(plan.getId()) && oldPlan.getBillingCycle() == plan.getBillingCycle())) {
                sub.setStatus(2); // expired
                subscriptionRepository.save(sub);
            }
        }

        // Nếu đã gia hạn monthly cùng loại, không cần tạo subscription mới
        if (hasSameMonthly) return;

        // Tạo subscription mới
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = plan.getBillingCycle() > 0 ? now.plusMonths(plan.getBillingCycle()) : null;
        int remainingQuota = plan.getBillingCycle() == 0 ? plan.getQuota() + leftoverQuota : 0;

        Subscription newSub = Subscription.builder()
                .accountId(accountId)
                .planId(plan.getId())
                .remainingQuota(remainingQuota)
                .startDate(now)
                .endDate(endDate)
                .status(1) // ACTIVE
                .build();

        Subscription savedSub = subscriptionRepository.save(newSub);

        // Chuyển sang DTO nếu cần
        SubscriptionMapper.toDto(savedSub);
    }
}