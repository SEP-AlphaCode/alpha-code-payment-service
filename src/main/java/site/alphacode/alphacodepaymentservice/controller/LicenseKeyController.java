package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/license-keys")
@RequiredArgsConstructor
@Tag(name = "License Keys", description = "License Key management APIs")
public class LicenseKeyController {
}
