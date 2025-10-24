package site.alphacode.alphacodepaymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatusEnum {
    PENDING(1, "CHỜ XỬ LÝ"),
    PAID(2, "ĐÃ THANH TOÁN"),
    CANCELLED(3, "ĐÃ HỦY");


    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (PaymentStatusEnum  s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
