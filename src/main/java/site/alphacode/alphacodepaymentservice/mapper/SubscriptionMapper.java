package site.alphacode.alphacodepaymentservice.mapper;

import site.alphacode.alphacodepaymentservice.dto.response.SubscriptionDto;
import site.alphacode.alphacodepaymentservice.entity.Subscription;

public class SubscriptionMapper {
    public static SubscriptionDto toDto(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .accountId(subscription.getAccountId())
                .planId(subscription.getPlanId())
                .remainingQuota(subscription.getRemainingQuota())
                .endDate(subscription.getEndDate())
                .startDate(subscription.getStartDate())
                .createdDate(subscription.getCreatedDate())
                .lastUpdated(subscription.getLastUpdated())
                .status(subscription.getStatus())
                .build();
    }
}
