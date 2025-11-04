package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.UserSubscriptionDashboard;

import java.util.UUID;

public interface SubscriptionService {
    void createOrUpdateSubscription(UUID accountId, UUID planId);
    UserSubscriptionDashboard getUserSubscriptionDashboard(UUID accountId);
}
