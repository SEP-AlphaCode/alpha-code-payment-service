package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.response.TokenRuleDto;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateTokenRule;
import site.alphacode.alphacodepaymentservice.dto.resquest.patch.PatchTokenRule;
import site.alphacode.alphacodepaymentservice.dto.resquest.update.UpdateTokenRule;

import java.util.UUID;

public interface TokenRuleService {
    TokenRuleDto createTokenRule(CreateTokenRule createTokenRule);
    TokenRuleDto updateTokenRule(UUID id, UpdateTokenRule updateTokenRule);
    TokenRuleDto getTokenRuleById(UUID id);
    void deleteTokenRule(UUID id);
    TokenRuleDto patch(UUID id, PatchTokenRule patchTokenRule);
    PagedResult<TokenRuleDto> getAlls(int page, int size, String searchTerm);
}
