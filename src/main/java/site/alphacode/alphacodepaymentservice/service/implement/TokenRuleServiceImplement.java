package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.response.SubscriptionPlanDto;
import site.alphacode.alphacodepaymentservice.dto.response.TokenRuleDto;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateTokenRule;
import site.alphacode.alphacodepaymentservice.dto.resquest.patch.PatchTokenRule;
import site.alphacode.alphacodepaymentservice.dto.resquest.update.UpdateTokenRule;
import site.alphacode.alphacodepaymentservice.entity.SubscriptionPlan;
import site.alphacode.alphacodepaymentservice.entity.TokenRule;
import site.alphacode.alphacodepaymentservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodepaymentservice.mapper.SubscriptionPlanMapper;
import site.alphacode.alphacodepaymentservice.mapper.TokenRuleMapper;
import site.alphacode.alphacodepaymentservice.repository.TokenRuleRepository;
import site.alphacode.alphacodepaymentservice.service.TokenRuleService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenRuleServiceImplement implements TokenRuleService {
    private final TokenRuleRepository tokenRuleRepository;

    @Override
    @Cacheable(value = "token_rule", key = "{#page, #size, #searchTerm}")
    public PagedResult<TokenRuleDto> getAlls(int page, int size, String searchTerm){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        Page<TokenRule> rules = tokenRuleRepository.findAll(searchTerm, pageable);
        Page<TokenRuleDto> dtoPage =  rules.map(TokenRuleMapper::toDto);
        return new PagedResult<>(dtoPage);
    }

    @Override
    @Transactional
    @CacheEvict(value = "token_rule", allEntries = true)
    public TokenRuleDto createTokenRule(CreateTokenRule createTokenRule) {
        // Kiểm tra xem code đã tồn tại chưa
        if (tokenRuleRepository.existsByCodeAndStatus(createTokenRule.getCode(), 1)) {
            throw new IllegalStateException("Token rule với code '" + createTokenRule.getCode() + "' đã tồn tại.");
        }

        TokenRule tokenRule = new TokenRule();
        tokenRule.setCode(createTokenRule.getCode());
        tokenRule.setCost(createTokenRule.getCost());
        tokenRule.setNote(createTokenRule.getNote());
        tokenRule.setCreatedDate(LocalDateTime.now());
        tokenRule.setStatus(1);
        tokenRule = tokenRuleRepository.save(tokenRule);

        return TokenRuleMapper.toDto(tokenRule);
    }

    @Override
    @Transactional
    @CacheEvict(value = "token_rule", allEntries = true)
    public TokenRuleDto updateTokenRule(UUID id, UpdateTokenRule updateTokenRule) {
        TokenRule tokenRule = tokenRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Token rule không tồn tại."));

        // Kiểm tra xem code mới đã tồn tại chưa (trừ record hiện tại)
        if (tokenRuleRepository.existsByCodeAndIdNot(updateTokenRule.getCode(), id)) {
            throw new IllegalStateException("Token rule với code '" + updateTokenRule.getCode() + "' đã tồn tại.");
        }

        tokenRule.setCode(updateTokenRule.getCode());
        tokenRule.setCost(updateTokenRule.getCost());
        tokenRule.setNote(updateTokenRule.getNote());
        tokenRule.setStatus(updateTokenRule.getStatus());
        tokenRule.setLastUpdated(LocalDateTime.now());
        tokenRule = tokenRuleRepository.save(tokenRule);

        return TokenRuleMapper.toDto(tokenRule);
    }

    @Override
    @Transactional
    @CacheEvict(value = "token_rule", allEntries = true)
    public TokenRuleDto patch(UUID id, PatchTokenRule patchTokenRule){
        TokenRule tokenRule = tokenRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Token rule không tồn tại."));

        // Kiểm tra xem code mới đã tồn tại chưa (trừ record hiện tại)
        if (patchTokenRule.getCode() != null && tokenRuleRepository.existsByCodeAndIdNot(patchTokenRule.getCode(), id)) {
            throw new IllegalStateException("Token rule với code '" + patchTokenRule.getCode() + "' đã tồn tại.");
        }

        if (patchTokenRule.getCode() != null) {
            tokenRule.setCode(patchTokenRule.getCode());
        }
        if (patchTokenRule.getCost() != null) {
            tokenRule.setCost(patchTokenRule.getCost());
        }
        if (patchTokenRule.getNote() != null) {
            tokenRule.setNote(patchTokenRule.getNote());
        }
        if(patchTokenRule.getStatus() != null){
            tokenRule.setStatus(patchTokenRule.getStatus());
        }
        tokenRule.setLastUpdated(LocalDateTime.now());
        tokenRule = tokenRuleRepository.save(tokenRule);

        return TokenRuleMapper.toDto(tokenRule);
    }

    @Override
    @Cacheable(value = "token_rule", key = "#id")
    public TokenRuleDto getTokenRuleById(UUID id) {
        TokenRule tokenRule = tokenRuleRepository.findByIdAndStatus(id, 1)
                .orElseThrow(() -> new ResourceNotFoundException("Token rule không tồn tại."));

        return TokenRuleMapper.toDto(tokenRule);
    }

    @Override
    @Transactional
    @CacheEvict(value = "token_rule", allEntries = true)
    public void deleteTokenRule(UUID id) {
        TokenRule tokenRule = tokenRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Token rule không tồn tại."));

        tokenRule.setStatus(0); // soft delete
        tokenRuleRepository.save(tokenRule);
    }
}
