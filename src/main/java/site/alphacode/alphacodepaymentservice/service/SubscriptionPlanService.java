package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.response.SubscriptionPlanDto;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.dto.resquest.patch.PatchSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.dto.resquest.update.UpdateSubscriptionPlan;

import java.util.UUID;

public interface SubscriptionPlanService {

    // Tạo mới plan
    SubscriptionPlanDto create(CreateSubscriptionPlan request);

    // Lấy plan theo ID
    SubscriptionPlanDto getById(UUID id);

    // Update toàn bộ plan
    SubscriptionPlanDto update(UUID id, UpdateSubscriptionPlan request);

    // Patch update từng trường
    SubscriptionPlanDto patch(UUID id, PatchSubscriptionPlan request);

    // Xóa mềm plan
    void delete(UUID id);

    PagedResult<SubscriptionPlanDto> getAllActivePlans(int page, int size, String search);

    PagedResult<SubscriptionPlanDto> getAllPlans(int page, int size, String search);
}
