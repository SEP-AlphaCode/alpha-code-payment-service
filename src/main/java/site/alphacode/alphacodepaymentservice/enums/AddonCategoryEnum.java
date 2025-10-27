package site.alphacode.alphacodepaymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AddonCategoryEnum {
    OSMO(1, "OSMO"),
    QRCODE(2, "QR CODE"),
    DANCEWITHMUSIC(3, "NHẢY THEO NHẠC"),
    JOYSTICKCONTROL(5, "ĐIỀU KHIỂN BẰNG CẦN ĐIỀU KHIỂN"),
    MULTIROBOTCONTROL(6, "ĐIỀU KHIỂN NHIỀU ROBOT"),
    SMARTHOME(7, "NHÀ THÔNG MINH");

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
