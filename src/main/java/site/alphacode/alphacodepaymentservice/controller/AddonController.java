package site.alphacode.alphacodepaymentservice.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.alphacode.alphacodepaymentservice.service.AddonService;

@RestController
@RequestMapping("/api/v1/addons")
@RequiredArgsConstructor
@Tag(name = "Addons", description = "Addon management APIs")
public class AddonController {
    private final AddonService addonService;

    //@GetMapping


}
