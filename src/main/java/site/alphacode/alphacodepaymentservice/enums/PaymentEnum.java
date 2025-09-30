package site.alphacode.alphacodepaymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentEnum {
    LICENSE_KEY(1, "MÃ BẢN QUYỀN"),
    ADD_ON(2, "DỊCH VỤ BỔ SUNG"),
    BUNDLE(3, "GÓI KHÓA HỌC"),
    COURSE(4, "KHÓA HỌC"),
    SUBSCRIPTION(5, "GÓI ĐĂNG KÝ DỊCH VỤ AI");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (PaymentEnum  s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
