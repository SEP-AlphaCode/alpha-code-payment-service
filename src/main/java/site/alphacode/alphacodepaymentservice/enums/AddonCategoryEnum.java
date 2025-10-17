package site.alphacode.alphacodepaymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AddonCategoryEnum {
    OSMO(1, "OSMO"),
    QRCODE(2, "QR CODE"),
    DANCEWITHMUSIC(3, "NHẢY THEO NHẠC"),
    BILLINGUAL(4, "NÓI SONG NGỮ"),
    JOYSTICKCONTROL(5, "ĐIỀU KHIỂN BẰNG CẦN ĐIỀU KHIỂN"),
    VOICECONTROL(6, "ĐIỀU KHIỂN BẰNG GIỌNG NÓI"),
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
