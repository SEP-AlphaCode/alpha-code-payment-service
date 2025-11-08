package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.AddonDto;
import site.alphacode.alphacodepaymentservice.dto.response.PagedResult;
import site.alphacode.alphacodepaymentservice.dto.request.create.CreateAddon;
import site.alphacode.alphacodepaymentservice.dto.request.patch.PatchAddon;
import site.alphacode.alphacodepaymentservice.dto.request.update.UpdateAddon;
import site.alphacode.alphacodepaymentservice.entity.Addon;
import site.alphacode.alphacodepaymentservice.exception.ConflictException;
import site.alphacode.alphacodepaymentservice.mapper.AddonMapper;
import site.alphacode.alphacodepaymentservice.repository.AddonRepository;
import site.alphacode.alphacodepaymentservice.service.AddonService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddonServiceImplement implements AddonService {
    private final AddonRepository addonRepository;

    @Override
    @Transactional
    @CacheEvict(value = "addons", allEntries = true)
    public AddonDto create(CreateAddon createAddon){
        var addon = addonRepository.existsAddonByName(createAddon.getName());
        if(addon){
            throw new ConflictException("Tên addon đã tồn tại");
        }

        var isExistByCategory = addonRepository.existsAddonByCategory(createAddon.getCategory());
        if(isExistByCategory){
            throw new ConflictException("Chỉ được phép có một addon cho mỗi danh mục");
        }

        var newAddon = new Addon();
        newAddon.setName(createAddon.getName());
        newAddon.setPrice(createAddon.getPrice());
        newAddon.setDescription(createAddon.getDescription());
        newAddon.setCreatedDate(LocalDateTime.now());
        newAddon.setCategory(createAddon.getCategory());
        newAddon.setStatus(1); // Mặc định là Active
        addonRepository.save(newAddon);

        return AddonMapper.toDto(newAddon);
    }

    @Override
    @Transactional
    @CachePut(value = "addon", key = "#id")
    @CacheEvict(value = {"addons", "active_addons"}, allEntries = true)
    public AddonDto update(UUID id, UpdateAddon updateAddon){
        var existing = addonRepository.findNoneDeletedById(id).orElseThrow(()
                -> new ConflictException("Không tìm thấy addon với id: " + id));

        if (!existing.getName().equals(updateAddon.getName())) {
            var addOnWithName = addonRepository.findNoneDeletedByName(updateAddon.getName());
            if (addOnWithName.isPresent() && !addOnWithName.get().getId().equals(updateAddon.getId())) {
                throw new ConflictException("Addon với tên: " + updateAddon.getName() + " đã tồn tại");
            }
        }

        if(!existing.getCategory().equals(updateAddon.getCategory())){
            var isExistByCategory = addonRepository.existsAddonByCategory(updateAddon.getCategory());
            if(isExistByCategory) throw new ConflictException("Chỉ được phép có một addon cho mỗi danh mục");
        }

        existing.setName(updateAddon.getName());
        existing.setPrice(updateAddon.getPrice());
        existing.setDescription(updateAddon.getDescription());
        existing.setCategory(updateAddon.getCategory());
        existing.setStatus(updateAddon.getStatus());
        existing.setLastUpdated(LocalDateTime.now());
        addonRepository.save(existing);
        return AddonMapper.toDto(existing);
    }

    @Override
    @Transactional
    @CachePut(value = "addon", key = "#id")
    @CacheEvict(value = {"addons","active_addons"}, allEntries = true)
    public AddonDto patch(UUID id, PatchAddon patchAddon){
        var existing = addonRepository.findNoneDeletedById(id).orElseThrow(()
                -> new ConflictException("Không tìm thấy addon với id: " + id));

        if (patchAddon.getName()  != null && !existing.getName().equals(patchAddon.getName())) {
            var addOnWithName = addonRepository.findNoneDeletedByName(patchAddon.getName());
            if (addOnWithName.isPresent() && !addOnWithName.get().getId().equals(id)) {
                throw new ConflictException("Addon với tên: " + patchAddon.getName() + " đã tồn tại");
            }
        }

        if(!existing.getCategory().equals(patchAddon.getCategory())){
            var isExistByCategory = addonRepository.existsAddonByCategory(patchAddon.getCategory());
            if(isExistByCategory) throw new ConflictException("Chỉ được phép có một addon cho mỗi danh mục");
        }

        if(patchAddon.getPrice() != null){
            existing.setPrice(patchAddon.getPrice());
        }

        if(patchAddon.getDescription() != null){
            existing.setDescription(patchAddon.getDescription());
        }

        if(patchAddon.getCategory() != null){
            existing.setCategory(patchAddon.getCategory());
        }

        if(patchAddon.getStatus() != null){
            existing.setStatus(patchAddon.getStatus());
        }

        existing.setLastUpdated(LocalDateTime.now());

        addonRepository.save(existing);
        return AddonMapper.toDto(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"addon", "addons", "active_addons"}, allEntries = true)
    public void delete(UUID id){
        var existing = addonRepository.findNoneDeletedById(id).orElseThrow(()
                -> new ConflictException("Không tìm thấy addon với id: " + id));

        existing.setStatus(0); // 0 = deleted
        existing.setLastUpdated(LocalDateTime.now());
        addonRepository.save(existing);
    }

    @Override
    @Cacheable(value = "addon", key = "#id")
    public AddonDto getNoneDeleteById(UUID id){
        var existing = addonRepository.findNoneDeletedById(id).orElseThrow(()
                -> new ConflictException("Không tìm thấy addon với id: " + id));

        return AddonMapper.toDto(existing);
    }

    @Override
    @Cacheable(value = "addon", key = "#id")
    public AddonDto getActiveById(UUID id){
        var existing = addonRepository.findByIdAndStatus(id, 1).orElseThrow(()
                -> new ConflictException("Không tìm thấy addon với id: " + id));

        return AddonMapper.toDto(existing);
    }

    @Override
    @Cacheable(value = "addons", key = "{#page, #size, #search}")
    public PagedResult<AddonDto> getNoneDeletedAddons(int page, int size, String search){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        var pagedResult = addonRepository.getAllNoneDeletedAddon(search, pageable);
        Page<AddonDto> dtoPage = pagedResult.map(AddonMapper::toDto);
        return new PagedResult<>(dtoPage);
    }

    @Override
    @Cacheable(value = "active_addons", key = "{#page, #size, #search}")
    public PagedResult<AddonDto> getActiveAddons(int page, int size, String search){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        var pagedResult = addonRepository.getAllActiveAddon(search, pageable);
        Page<AddonDto> dtoPage = pagedResult.map(AddonMapper::toDto);
        return new PagedResult<>(dtoPage);
    }
}
