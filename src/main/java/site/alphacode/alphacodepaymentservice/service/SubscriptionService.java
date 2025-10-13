package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.SubscriptionDto;

import java.util.UUID;

public interface SubscriptionService {
    SubscriptionDto createOrUpdateSubscription(UUID accountId, UUID planId);
}
