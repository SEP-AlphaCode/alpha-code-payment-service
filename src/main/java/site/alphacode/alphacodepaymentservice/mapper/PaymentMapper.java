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
                .licenseKeyAddonId(payment.getLicenseKeyAddonId())
                .courseId(payment.getCourseId())
                .bundleId(payment.getBundleId())
                .planId(payment.getPlanId())
                .licenseKeyId(payment.getLicenseKeyId())
                .createdDate(payment.getCreatedDate())
                .lastUpdated(payment.getLastUpdated())
                .build();
    }
}
