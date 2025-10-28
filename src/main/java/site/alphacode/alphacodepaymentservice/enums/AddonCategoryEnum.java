package site.alphacode.alphacodepaymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AddonCategoryEnum {
    OSMO(1, "OSMO"),
    QRCODE(2, "QR CODE"),
    SMARTHOME(3, "NHÀ THÔNG MINH"),
    BLOCKLY(4, "LẬP TRÌNH BLOCKLY");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (AddonCategoryEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
