package site.alphacode.alphacodepaymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentCategoryEnum {
    COURSE(1, "KHÓA HỌC"),
    BUNDLE(2, "GÓI KHÓA HỌC"),
    ADD_ON(3, "DỊCH VỤ BỔ SUNG"),
    SUBSCRIPTIONPLAN(4, "GÓI ĐĂNG KÝ DỊCH VỤ AI"),
    LICENSE_KEY(5, "MÃ BẢN QUYỀN");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (PaymentCategoryEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
