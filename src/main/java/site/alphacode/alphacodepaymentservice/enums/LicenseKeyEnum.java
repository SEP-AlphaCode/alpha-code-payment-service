package site.alphacode.alphacodepaymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LicenseKeyEnum {
    ACTIVE(1, "ĐANG HOẠT ĐỘNG"),
    INACTIVE(2, "KHÔNG HOẠT ĐỘNG");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (LicenseKeyEnum  s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
