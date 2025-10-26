package site.alphacode.alphacodepaymentservice.mapper;

import site.alphacode.alphacodepaymentservice.dto.response.KeyPriceDto;
import site.alphacode.alphacodepaymentservice.entity.KeyPrice;

public class KeyPriceMapper {
    public static KeyPriceDto toDto(KeyPrice keyPrice) {
        if (keyPrice == null) {
            return null;
        }
        return KeyPriceDto.builder()
                .id(keyPrice.getId())
                .price(keyPrice.getPrice())
                .createdDate(keyPrice.getCreatedDate())
                .lastUpdated(keyPrice.getLastUpdated())
                .status(keyPrice.getStatus())
                .build();
    }
}
