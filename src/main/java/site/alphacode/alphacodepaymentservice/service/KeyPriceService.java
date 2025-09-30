package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.KeyPriceDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface KeyPriceService {
    KeyPriceDto getKeyPrice();
    KeyPriceDto createKeyPrice(BigDecimal price);
    KeyPriceDto updateKeyPrice(UUID id, BigDecimal price);
    void deleteKeyPrice(UUID id);
}
