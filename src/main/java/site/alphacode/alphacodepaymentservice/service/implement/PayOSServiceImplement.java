package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.request.create.PayOSEmbeddedLinkRequest;

import site.alphacode.alphacodepaymentservice.entity.Payment;
import site.alphacode.alphacodepaymentservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodepaymentservice.grpc.client.CourseServiceClient;
import site.alphacode.alphacodepaymentservice.producer.PaymentProducer;
import site.alphacode.alphacodepaymentservice.repository.AddonRepository;
import site.alphacode.alphacodepaymentservice.repository.PaymentRepository;
import site.alphacode.alphacodepaymentservice.repository.SubscriptionPlanRepository;
import site.alphacode.alphacodepaymentservice.service.*;
import vn.payos.PayOS;
import vn.payos.type.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PayOSServiceImplement implements PayOSService {
    private final LicenseKeyService licenseKeyService;
    private final LicenseKeyAddonService licenseKeyAddonService;
    private final SubscriptionService subscriptionService;
    private final CourseServiceClient courseServiceClient;
    private final AddonRepository addonRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    @Value("${payos.client.id}")
    private String clientId;

    @Value("${payos.api.key}")
    private String apiKey;

    @Value("${payos.checksum.key}")
    private String checkSumKey;

    @Value("${return-url-payment}")
    private String returnUrlPayment;

    @Value("${cancel-url-payment}")
    private String cancelUrlPayment;

    private PayOS payOS;

    @PostConstruct
    public void init() {
        this.payOS = new PayOS(clientId, apiKey, checkSumKey);
    }

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    @Override
    @Transactional
    public CheckoutResponseData createEmbeddedLink(PayOSEmbeddedLinkRequest payOSEmbeddedLinkRequest, Long orderCode) throws Exception {

        ItemData itemData = ItemData
                .builder()
                .name(payOSEmbeddedLinkRequest.getName())
                .quantity(1)
                .price(payOSEmbeddedLinkRequest.getPrice())
                .build();

        PaymentData paymentData = PaymentData
                .builder()
                .orderCode(orderCode)
                .amount(payOSEmbeddedLinkRequest.getPrice())
                .description("Thanh toán đơn hàng")
                .returnUrl(returnUrlPayment)
                .cancelUrl(cancelUrlPayment)
                .item(itemData)
                .expiredAt(300)
                .build();

        return payOS.createPaymentLink(paymentData);
    }

    @Override
    public PaymentLinkData getPaymentLinkInformation(Long orderCode) throws Exception {
        return payOS.getPaymentLinkInformation(orderCode);
    }

    @Override
    public PaymentLinkData cancelPaymentLink(Long orderCode, String cancelReason) throws Exception {
        var payment = paymentRepository.findByOrderCode(orderCode);
        if (payment.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy Payment với orderCode: " + orderCode);
        }

        if (payment.get().getStatus() == 2) { // 2 = PAID
            throw new IllegalStateException("Không thể hủy Payment đã được thanh toán");
        }

        payment.get().setStatus(3); // 3 = CANCELLED
        payment.get().setLastUpdated(LocalDateTime.now());
        paymentRepository.save(payment.get());

        return payOS.cancelPaymentLink(orderCode, cancelReason);
    }

    @Override
    public String confirmWebhook(String webHookUrl) throws  Exception {
        return payOS.confirmWebhook(webHookUrl);
    }

    @Override
    @Transactional
    public WebhookData processWebhook(Webhook webhook) throws Exception {
        // 1. Verify chữ ký
        WebhookData webhookData = payOS.verifyPaymentWebhookData(webhook);
        if (webhookData == null) {
            throw new IllegalArgumentException("Webhook không hợp lệ hoặc chữ ký sai");
        }

        Long orderCode = webhookData.getOrderCode();
        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Payment với orderCode: " + orderCode));

        // 2. Mapping trạng thái dựa vào data.code
        if ("00".equals(webhookData.getCode())) {
            payment.setStatus(2); // PAID

            String serviceName;

            // --- 2a. Xác định loại dịch vụ để gửi queue đúng ---
            if (payment.getCourseId() != null) {
                var courseInfo = courseServiceClient.getCourseInformation(payment.getCourseId().toString());
                serviceName = courseInfo.getName().isEmpty() ? "Khóa học" : courseInfo.getName();
                paymentProducer.sendCourseCreated(payment.getCourseId().toString(), payment.getAccountId().toString(), orderCode);
                paymentProducer.sendNotification(payment.getAccountId().toString(), payment.getOrderCode(), serviceName, payment.getAmount());
            } else if (payment.getBundleId() != null) {
                var bundleInfo = courseServiceClient.getBundleInformation(payment.getBundleId().toString());
                serviceName = bundleInfo.getName().isEmpty() ? "Gói học" : bundleInfo.getName();
                paymentProducer.sendBundleCreated(payment.getBundleId().toString(), payment.getAccountId().toString(), orderCode);
                paymentProducer.sendNotification(payment.getAccountId().toString(), payment.getOrderCode(), serviceName, payment.getAmount());
            } else if (payment.getLicenseKeyAddonId() != null) {
                var licenseKeyAddonInfo = licenseKeyAddonService.getById(payment.getLicenseKeyAddonId());
                var addonInfo = addonRepository.findById(licenseKeyAddonInfo.getAddonId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy addon"));
                serviceName = addonInfo.getName();
                // Find license key by accountId
                var licenseKey = licenseKeyService.getLicenseByAccountId(payment.getAccountId());
                if (licenseKey == null) {
                    throw new RuntimeException("Không tìm thấy LicenseKey cho accountId: " + payment.getAccountId());
                }

                licenseKeyAddonService.activate(licenseKeyAddonInfo.getId());

                paymentProducer.sendNotification(payment.getAccountId().toString(), payment.getOrderCode(), serviceName, payment.getAmount());
            } else if (payment.getPlanId() != null) {
                var subscriptionInfo = subscriptionPlanRepository.findById(payment.getPlanId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy subscription plan"));
                serviceName = subscriptionInfo.getName();
                subscriptionService.createOrUpdateSubscription(payment.getAccountId(), payment.getPlanId());
                paymentProducer.sendNotification(payment.getAccountId().toString(), payment.getOrderCode(), serviceName, payment.getAmount());
            } else if (payment.getLicenseKeyId() != null) {
                licenseKeyService.activateLicense(payment.getLicenseKeyId());
                paymentProducer.sendNotification(payment.getAccountId().toString(), payment.getOrderCode(), "Mua license key", payment.getAmount());
            }

        } else {
            payment.setStatus(3); // FAILED/CANCELLED
        }

        // 3. Lưu mô tả lỗi/thành công để debug
        payment.setNote(webhookData.getDesc());
        payment.setLastUpdated(LocalDateTime.now());

        paymentRepository.save(payment);

        return  webhookData;
    }

}
