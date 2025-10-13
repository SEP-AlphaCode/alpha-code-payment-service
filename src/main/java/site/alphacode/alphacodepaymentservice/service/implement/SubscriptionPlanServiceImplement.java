package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.response.SubscriptionPlanDto;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.dto.resquest.patch.PatchSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.dto.resquest.update.UpdateSubscriptionPlan;
import site.alphacode.alphacodepaymentservice.entity.SubscriptionPlan;
import site.alphacode.alphacodepaymentservice.exception.ConflictException;
import site.alphacode.alphacodepaymentservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodepaymentservice.mapper.SubscriptionPlanMapper;
import site.alphacode.alphacodepaymentservice.repository.SubscriptionPlanRepository;
import site.alphacode.alphacodepaymentservice.service.SubscriptionPlanService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImplement implements SubscriptionPlanService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final Logger logger = LoggerFactory.getLogger(SubscriptionPlanServiceImplement.class);

    @Transactional
    @CachePut(value = "subscription_plan", key = "{#result.id}")
    @Caching(evict = {
            @CacheEvict(value = "all_subscription_plans", allEntries = true)
    })
    public SubscriptionPlanDto create(CreateSubscriptionPlan request) {
        try {
            if (subscriptionPlanRepository.findByNameAndStatus(request.getName(), 1) != null) {
                throw new ConflictException("Gói đăng ký với tên này đã tồn tại.");
            }

            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .billingCycle(request.getBillingCycle())
                    .quota(request.getQuota())
                    .status(1) // ACTIVE
                    .createdDate(LocalDateTime.now())
                    .build();

            SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
            return SubscriptionPlanMapper.toDto(saved);

        } catch (Exception e) {
            logger.error("Lỗi khi tạo SubscriptionPlan: {}", e.getMessage());
            throw e;
        }
    }

    @Cacheable(value = "subscription_plan", key = "{#id}")
    public SubscriptionPlanDto getById(UUID id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gói đăng ký với ID " + id + " không tồn tại."));
        return SubscriptionPlanMapper.toDto(plan);
    }

    @Cacheable(value = "all_subscription_plans", key = "{#page, #size, #search}")
    public PagedResult<SubscriptionPlanDto> getAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        Page<SubscriptionPlan> plans = subscriptionPlanRepository.findAllByNameContainingAndStatus(search, 1, pageable);
        Page<SubscriptionPlanDto> dtoPage = plans.map(SubscriptionPlanMapper::toDto);
        return new PagedResult<>(dtoPage);
    }

        @Transactional
    @CachePut(value = "subscription_plan", key = "{#id}")
    @Caching(evict = {
            @CacheEvict(value = "all_subscription_plans", allEntries = true)
    })
    public SubscriptionPlanDto update(UUID id, UpdateSubscriptionPlan request) {
        try {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Gói đăng ký với ID " + id + " không tồn tại."));

            if (subscriptionPlanRepository.findByNameAndStatus(request.getName(), 1) != null
                    && !plan.getName().equals(request.getName())) {
                throw new ConflictException("Gói đăng ký với tên này đã tồn tại.");
            }

            plan.setName(request.getName());
            plan.setDescription(request.getDescription());
            plan.setPrice(request.getPrice());
            plan.setBillingCycle(request.getBillingCycle());
            plan.setQuota(request.getQuota());
            plan.setLastUpdated(LocalDateTime.now());

            SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
            return SubscriptionPlanMapper.toDto(saved);

        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật SubscriptionPlan: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    @CachePut(value = "subscription_plan", key = "{#id}")
    @Caching(evict = {
            @CacheEvict(value = "all_subscription_plans", allEntries = true)
    })
    public SubscriptionPlanDto patch(UUID id, PatchSubscriptionPlan request) {
        try {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Gói đăng ký với ID " + id + " không tồn tại."));

            if (request.getName() != null && !request.getName().isBlank()) {
                plan.setName(request.getName());
            }
            if (request.getDescription() != null) {
                plan.setDescription(request.getDescription());
            }
            if (request.getPrice() != null) {
                plan.setPrice(request.getPrice());
            }
            if (request.getBillingCycle() != null) {
                plan.setBillingCycle(request.getBillingCycle());
            }
            if (request.getQuota() != null) {
                plan.setQuota(request.getQuota());
            }

            plan.setLastUpdated(LocalDateTime.now());
            SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
            return SubscriptionPlanMapper.toDto(saved);

        } catch (Exception e) {
            logger.error("Lỗi khi patch SubscriptionPlan: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "subscription_plan", key = "{#id}"),
            @CacheEvict(value = "all_subscription_plans", allEntries = true)
    })
    public void delete(UUID id) {
        try {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Gói đăng ký với ID " + id + " không tồn tại."));
            plan.setStatus(0); // soft delete
            plan.setLastUpdated(LocalDateTime.now());
            subscriptionPlanRepository.save(plan);
        } catch (Exception e) {
            logger.error("Lỗi khi xóa SubscriptionPlan: {}", e.getMessage());
            throw e;
        }
    }
}
