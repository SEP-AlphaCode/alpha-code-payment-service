package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.KeyPriceDto;
import site.alphacode.alphacodepaymentservice.entity.KeyPrice;
import site.alphacode.alphacodepaymentservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodepaymentservice.mapper.KeyPriceMapper;
import site.alphacode.alphacodepaymentservice.repository.KeyPriceRepository;
import site.alphacode.alphacodepaymentservice.service.KeyPriceService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeyPriceServiceImplement implements KeyPriceService {
    private final KeyPriceRepository keyPriceRepository;

    @Override
    @Transactional
    @CacheEvict(value = "key_price", allEntries = true)
    public KeyPriceDto createKeyPrice(BigDecimal price) {
        if(keyPriceRepository.count() > 0) {
            throw new IllegalStateException("Key price đã tồn tại. Không thể tạo mới.");
        }

        KeyPrice keyPrice = new KeyPrice();
        keyPrice.setPrice(price);
        keyPrice.setCreatedDate(LocalDateTime.now());
        keyPrice = keyPriceRepository.save(keyPrice);

        return KeyPriceMapper.toDto(keyPrice);
    }

    @Override
    @Transactional
    @CacheEvict(value = "key_price", allEntries = true)
    public KeyPriceDto updateKeyPrice(UUID id, BigDecimal price) {
        KeyPrice keyPrice = keyPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Key price không tồn tại."));

        keyPrice.setPrice(price);
        keyPrice.setLastUpdated(LocalDateTime.now());
        keyPrice = keyPriceRepository.save(keyPrice);

        return KeyPriceMapper.toDto(keyPrice);
    }

    @Override
    @Cacheable(value = "key_price")
    public KeyPriceDto getKeyPrice() {
        KeyPrice keyPrice = keyPriceRepository.findTopByOrderByCreatedDateDesc()
                .orElseThrow(() -> new ResourceNotFoundException("Key price không tồn tại."));

        return KeyPriceMapper.toDto(keyPrice);
    }

    @Override
    @Transactional
    @CacheEvict(value = "key_price", allEntries = true)
    public void deleteKeyPrice(UUID id) {
        KeyPrice keyPrice = keyPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Key price không tồn tại."));

        keyPriceRepository.delete(keyPrice);
    }
}
