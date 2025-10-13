package site.alphacode.alphacodepaymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionEnum {
    ACTIVE(1, "ĐANG HOẠT ĐỘNG"),
    EXPIRED(2, "ĐÃ HẾT HẠN");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (SubscriptionEnum  s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
