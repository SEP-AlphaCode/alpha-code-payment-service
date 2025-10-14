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

    public void createOrUpdateSubscription(UUID accountId, UUID planId) {
        // 1. Lấy plan
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // 2. Kết thúc subscription cũ còn active
        List<Subscription> currentSubs = subscriptionRepository
                .findByAccountIdAndStatus(accountId, 1); // 1 = ACTIVE

        int leftoverQuota = 0;
        for (Subscription sub : currentSubs) {
            leftoverQuota += sub.getRemainingQuota(); // cộng quota dư
            sub.setStatus(2); // 2 = EXPIRED
            subscriptionRepository.save(sub);
        }

        // 3. Tạo subscription mới
        Subscription newSub = Subscription.builder()
                .accountId(accountId)
                .planId(plan.getId())
                .remainingQuota(plan.getQuota() + leftoverQuota) // cộng quota dư
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(plan.getBillingCycle())) // dùng billingCycle
                .status(1) // 1 = ACTIVE
                .build();

        Subscription savedSub = subscriptionRepository.save(newSub);

        // 4. Chuyển sang DTO
        SubscriptionMapper.toDto(savedSub);
    }

}
