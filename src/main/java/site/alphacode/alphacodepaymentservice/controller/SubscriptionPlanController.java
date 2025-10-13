package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.response.SubscriptionPlanDto;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.dto.resquest.patch.PatchSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.dto.resquest.update.UpdateSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.service.SubscriptionPlanService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscription-plans")
@RequiredArgsConstructor
@Tag(name = "Subscription Plans", description = "Subscription Plan management APIs")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription plan by id")
    public SubscriptionPlanDto getById(@PathVariable UUID id) {
        return subscriptionPlanService.getById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new subscription plan")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public SubscriptionPlanDto create(@Valid @ModelAttribute @RequestBody CreateSubscriptionPlan request) {
        return subscriptionPlanService.create(request);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update subscription plan by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public SubscriptionPlanDto update(@PathVariable UUID id,
                                      @Valid @ModelAttribute @RequestBody UpdateSubscriptionPlan request) {
        return subscriptionPlanService.update(id, request);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Patch subscription plan by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public SubscriptionPlanDto patch(@PathVariable UUID id,
                                     @Valid @ModelAttribute @RequestBody PatchSubscriptionPlan request) {
        return subscriptionPlanService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subscription plan by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void delete(@PathVariable UUID id) {
        subscriptionPlanService.delete(id);
    }

    @GetMapping()
    @Operation(summary = "Get all subscription plans with pagination and optional search")
    public PagedResult<SubscriptionPlanDto> getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search
    ) {
        return subscriptionPlanService.getAll(page, size, search);
    }
}
