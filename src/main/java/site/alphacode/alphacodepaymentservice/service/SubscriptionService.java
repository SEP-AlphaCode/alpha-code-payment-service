package site.alphacode.alphacodepaymentservice.service;

import java.util.UUID;

public interface SubscriptionService {
    void createOrUpdateSubscription(UUID accountId, UUID planId);
}
