package site.alphacode.alphacodepaymentservice.mapper;

import site.alphacode.alphacodepaymentservice.dto.response.PaymentDto;
import site.alphacode.alphacodepaymentservice.entity.Payment;

public class PaymentMapper {
    public static PaymentDto toDto(Payment payment) {
        if(payment == null) {
            return null;
        }

        return PaymentDto.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .category(payment.getCategory())
                .paymentMethod(payment.getPaymentMethod())
                .accountId(payment.getAccountId())
                .addonId(payment.getAddonId())
                .courseId(payment.getCourseId())
                .bundleId(payment.getBundleId())
                .subscriptionId(payment.getSubscriptionId())
                .createdDate(payment.getCreatedDate())
                .lastUpdated(payment.getLastUpdated())
                .build();
    }
}
