package site.alphacode.alphacodepaymentservice.mapper;

import site.alphacode.alphacodepaymentservice.dto.response.TokenRuleDto;
import site.alphacode.alphacodepaymentservice.entity.TokenRule;

public class TokenRuleMapper {
    public static TokenRuleDto toDto(TokenRule tokenRule) {
        if (tokenRule == null) {
            return null;
        }

        return TokenRuleDto.builder()
                .id(tokenRule.getId())
                .code(tokenRule.getCode())
                .cost(tokenRule.getCost())
                .note(tokenRule.getNote())
                .status(tokenRule.getStatus())
                .createdDate(tokenRule.getCreatedDate())
                .lastUpdated(tokenRule.getLastUpdated())
                .build();
    }

    public static TokenRule toEntity(TokenRuleDto tokenRuleDto) {
        if (tokenRuleDto == null) {
            return null;
        }

        return TokenRule.builder()
                .id(tokenRuleDto.getId())
                .code(tokenRuleDto.getCode())
                .cost(tokenRuleDto.getCost())
                .note(tokenRuleDto.getNote())
                .status(tokenRuleDto.getStatus())
                .createdDate(tokenRuleDto.getCreatedDate())
                .lastUpdated(tokenRuleDto.getLastUpdated())
                .build();
    }
}
