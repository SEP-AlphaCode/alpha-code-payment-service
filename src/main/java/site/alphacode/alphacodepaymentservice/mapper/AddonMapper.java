package site.alphacode.alphacodepaymentservice.mapper;

import site.alphacode.alphacodepaymentservice.dto.response.AddonDto;
import site.alphacode.alphacodepaymentservice.entity.Addon;

public class AddonMapper {
    public static AddonDto toDto(Addon addon) {
        if (addon == null) {
            return null;
        }
        return AddonDto.builder()
                .id(addon.getId())
                .name(addon.getName())
                .description(addon.getDescription())
                .price(addon.getPrice())
                .createdDate(addon.getCreatedDate())
                .lastUpdated(addon.getLastUpdated())
                .status(addon.getStatus())
                .category(addon.getCategory())
                .build();
    }
}
