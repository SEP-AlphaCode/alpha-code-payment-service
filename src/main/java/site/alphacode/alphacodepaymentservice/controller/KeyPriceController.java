package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodepaymentservice.dto.response.KeyPriceDto;
import site.alphacode.alphacodepaymentservice.service.KeyPriceService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/key-prices")
@RequiredArgsConstructor
@Tag(name = "Key Prices", description = "Key Price management APIs")
public class KeyPriceController {
    private final KeyPriceService keyPriceService;

    @GetMapping()
    @Operation(summary = "Get key price")
    public KeyPriceDto getKeyPrice() {
        return keyPriceService.getKeyPrice();
    }

    @PostMapping()
    @Operation(summary = "Create key price")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public KeyPriceDto createKeyPrice(@RequestParam Integer price) {
        return keyPriceService.createKeyPrice(price);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update key price")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public KeyPriceDto updateKeyPrice(@PathVariable UUID id,@RequestParam Integer price) {
        return keyPriceService.updateKeyPrice(id, price);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete key price")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public void deleteKeyPrice(@PathVariable UUID id) {
        keyPriceService.deleteKeyPrice(id);
    }
}
