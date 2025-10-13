package site.alphacode.alphacodepaymentservice.mapper;

import site.alphacode.alphacodepaymentservice.dto.response.SubscriptionPlanDto;
import site.alphacode.alphacodepaymentservice.entity.SubscriptionPlan;

public class SubscriptionPlanMapper {
    public static SubscriptionPlanDto toDto(SubscriptionPlan subscriptionPlan) {
        if (subscriptionPlan == null) {
            return null;
        }
        return SubscriptionPlanDto.builder()
                .id(subscriptionPlan.getId())
                .name(subscriptionPlan.getName())
                .description(subscriptionPlan.getDescription())
                .price(subscriptionPlan.getPrice())
                .billingCycle(subscriptionPlan.getBillingCycle())
                .createdDate(subscriptionPlan.getCreatedDate())
                .lastUpdated(subscriptionPlan.getLastUpdated())
                .quota(subscriptionPlan.getQuota())
                .status(subscriptionPlan.getStatus())
                .build();
    }
}
