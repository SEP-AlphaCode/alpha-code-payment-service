package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.LicenseKeyAddonDto;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreateLincenseKeyAddon;

public interface LicenseKeyAddonService {
    LicenseKeyAddonDto create(CreateLincenseKeyAddon createLincenseKeyAddon);
}
