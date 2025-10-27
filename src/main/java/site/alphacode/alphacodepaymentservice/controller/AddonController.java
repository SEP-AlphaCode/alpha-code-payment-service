package site.alphacode.alphacodepaymentservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodepaymentservice.dto.response.AddonDto;
import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateAddon;
import site.alphacode.alphacodepaymentservice.dto.resquest.patch.PatchAddon;
import site.alphacode.alphacodepaymentservice.dto.resquest.update.UpdateAddon;
import site.alphacode.alphacodepaymentservice.service.AddonService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addons")
@RequiredArgsConstructor
@Tag(name = "Addons", description = "Addon management APIs")
public class AddonController {
    private final AddonService addonService;

    @GetMapping()
    @Operation(summary = "Get all active addons")
    public PagedResult<AddonDto> getActiveAddons( @RequestParam(value = "page", defaultValue = "1") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  @RequestParam(value = "search", defaultValue = "") String search) {
        return addonService.getActiveAddons(page, size, search);
    }

    @GetMapping("/none-deleted")
    @Operation(summary = "Get all none-deleted addons")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public PagedResult<AddonDto> getNoneDeletedAddons( @RequestParam(value = "page", defaultValue = "1") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                                       @RequestParam(value = "search", defaultValue = "") String search) {
        return addonService.getNoneDeletedAddons(page, size, search);
    }

    @GetMapping("/active/{id}")
    @Operation(summary = "Get active addon by id")
    public AddonDto getActiveAddonById(@PathVariable("id") UUID id) {
        return addonService.getActiveById(id);
    }

    @GetMapping("/none-deleted/{id}")
    @Operation(summary = "Get none-deleted addon by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public AddonDto getNoneDeletedAddonById(@PathVariable("id") UUID id) {
        return addonService.getNoneDeleteById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete addon by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void deleteAddonById(@PathVariable("id") UUID id) {
        addonService.delete(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch addon by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public AddonDto patchAddonById(@PathVariable("id") UUID id, @RequestBody PatchAddon patchAddon) {
        return addonService.patch(id, patchAddon);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update addon by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public AddonDto updateAddonById(@PathVariable("id") UUID id, @RequestBody UpdateAddon addonDto) {
        return addonService.update(id, addonDto);
    }

    @PostMapping()
    @Operation(summary = "Create new addon")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public AddonDto createAddon(@RequestBody CreateAddon addonDto) {
        return addonService.create(addonDto);
    }

}
