package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.KeyPriceDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface KeyPriceService {
    KeyPriceDto getKeyPrice();
    KeyPriceDto createKeyPrice(Integer price);
    KeyPriceDto updateKeyPrice(UUID id, Integer price);
    void deleteKeyPrice(UUID id);
}
