package site.alphacode.alphacodepaymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AddonEnum {
    DELETED(0, "ĐÃ XÓA"),
    ACTIVE(1, "ĐANG HOẠT ĐỘNG"),
    INACTIVE(2, "KHÔNG HOẠT ĐỘNG"),
    OSMO(3, "OSMO"),
    QRCODE(4, "QR CODE"),
    DANCEWITHMUSIC(5, "NHẢY THEO NHẠC"),
    BILLINGUAL(6, "NÓI SONG NGỮ"),
    JOYSTICKCONTROL(7, "ĐIỀU KHIỂN BẰNG CẦN ĐIỀU KHIỂN"),
    VOICECONTROL(8, "ĐIỀU KHIỂN BẰNG GIỌNG NÓI"),
    SMARTHOME(9, "NHÀ THÔNG MINH");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (AddonEnum  s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
