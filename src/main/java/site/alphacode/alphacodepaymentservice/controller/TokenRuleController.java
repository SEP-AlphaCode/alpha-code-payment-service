package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.response.TokenRuleDto;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateTokenRule;
import site.alphacode.alphacodepaymentservice.dto.resquest.patch.PatchTokenRule;
import site.alphacode.alphacodepaymentservice.dto.resquest.update.UpdateTokenRule;
import site.alphacode.alphacodepaymentservice.service.TokenRuleService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/token-rules")
@RequiredArgsConstructor
@Tag(name = "Token Rules", description = "Token Rule management APIs")
public class TokenRuleController {
    private final TokenRuleService tokenRuleService;

    @GetMapping("/{id}")
    @Operation(summary = "Get token rule by id")
    public TokenRuleDto getTokenRuleById(@PathVariable UUID id) {
        return tokenRuleService.getTokenRuleById(id);
    }

    @PostMapping
    @Operation(summary = "Create new token rule")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public TokenRuleDto create(@Valid @RequestBody CreateTokenRule request) {
        return tokenRuleService.createTokenRule(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update token rule")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public TokenRuleDto update(@PathVariable UUID id, @Valid @RequestBody UpdateTokenRule request) {
        return tokenRuleService.updateTokenRule(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch update token rule")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public TokenRuleDto patch(@PathVariable UUID id, @Valid @RequestBody PatchTokenRule request) {
        return tokenRuleService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete token rule")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public void delete(@PathVariable UUID id) {
        tokenRuleService.deleteTokenRule(id);
    }

    @GetMapping()
    @Operation(summary = "Get all token rule")
    public PagedResult<TokenRuleDto> getAllTokenRules(@RequestParam(value = "page", defaultValue = "1") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                                      @RequestParam(value = "search", required = false) String search) {
        return tokenRuleService.getAlls(page, size, search);
    }
}
