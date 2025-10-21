package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.AddonDto;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateAddon;
import site.alphacode.alphacodepaymentservice.dto.resquest.patch.PatchAddon;
import site.alphacode.alphacodepaymentservice.dto.resquest.update.UpdateAddon;

import java.util.UUID;

public interface AddonService {
    AddonDto create(CreateAddon createAddon);
    AddonDto update(UUID id, UpdateAddon updateAddon);
    AddonDto patch(UUID id, PatchAddon patchAddon);
    void delete(UUID id);
    AddonDto getNoneDeleteById(UUID id);

    AddonDto getActiveById(UUID id);
}
