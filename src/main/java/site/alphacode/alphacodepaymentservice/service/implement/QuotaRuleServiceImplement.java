package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuotaRuleServiceImplement implements QuotaRuleService {
    private final QuotaRuleRepository quotaRuleRepository;

    @Override
    @Transactional
    @CacheEvict(value = "quota_rule", allEntries = true)
    public QuotaRuleDto createQuotaRule(Integer amount) {
        if(quotaRuleRepository.count() > 0) {
            throw new IllegalStateException("Amount cho quota đã tồn tại. Không thể tạo mới.");
        }

        QuotaRule data = new QuotaRule();
        data.setAmount(amount);
        data.setCreatedDate(LocalDateTime.now());
        data = quotaRuleRepository.save(data);

        return QuotaMapper.toDto(data);
    }

    @Override
    @Transactional
    @CacheEvict(value = "quota_rule", allEntries = true)
    public QuotaRuleDto updateQuota(UUID id, Integer price) {
        QuotaRule data = quotaRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quota rule không tồn tại."));

        data.setAmount(price);
        data.setLastUpdated(LocalDateTime.now());
        data = quotaRuleRepository.save(data);

        return QuotaMapper.toDto(data);
    }

    @Override
    @Cacheable(value = "quota_rule")
    public QuotaRuleDto getQuotaRule() {
        QuotaRule data = quotaRuleRepository.findTopByOrderByCreatedDateDesc()
                .orElseThrow(() -> new ResourceNotFoundException("Quota rule không tồn tại."));

        return QuotaMapper.toDto(data);
    }

    @Override
    @Transactional
    @CacheEvict(value = "quota_rule", allEntries = true)
    public void deleteQuotaRule(UUID id) {
        QuotaRule data = quotaRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Key price không tồn tại."));

        quotaRuleRepository.delete(data);
    }

}
