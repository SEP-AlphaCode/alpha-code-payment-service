package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.alphacode.alphacodepaymentservice.dto.response.UserSubscriptionDashboard;
import site.alphacode.alphacodepaymentservice.service.SubscriptionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscription management APIs")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/dashboard/{accountId}")
    @Operation(summary = "Get User Subscription Dashboard", description = "Retrieve the subscription dashboard for a specific user account.")
    public UserSubscriptionDashboard getUserSubscriptionDashboard(@PathVariable UUID accountId) {
        return subscriptionService.getUserSubscriptionDashboard(accountId);
    }

}
