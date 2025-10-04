package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.PayOSEmbeddedLinkRequest;

import site.alphacode.alphacodepaymentservice.entity.Payment;
import site.alphacode.alphacodepaymentservice.producer.PaymentProducer;
import site.alphacode.alphacodepaymentservice.repository.PaymentRepository;
import site.alphacode.alphacodepaymentservice.service.PayOSService;
import vn.payos.PayOS;
import vn.payos.type.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PayOSServiceImplement implements PayOSService {
    @Value("${payos.client.id}")
    private String clientId;

    @Value("${payos.api.key}")
    private String apiKey;

    @Value("${payos.checksum.key}")
    private String checkSumKey;

    @Value("${web-base-url}")
    private String webUrl;

    private PayOS payOS;

    @PostConstruct
    public void init() {
        this.payOS = new PayOS(clientId, apiKey, checkSumKey);
    }

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

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
                .returnUrl(webUrl)
                .cancelUrl(webUrl)
                .item(itemData)
                .build();

        return payOS.createPaymentLink(paymentData);
    }

    @Transactional
    public void processWebhook(Webhook webhook) throws Exception {
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

            // --- 2a. Xác định loại dịch vụ để gửi queue đúng ---
            if (payment.getCourseId() != null) {
                paymentProducer.sendCourseCreated(payment.getCourseId().toString(), payment.getAccountId().toString(), orderCode);
            } else if (payment.getBundleId() != null) {
                paymentProducer.sendBundleCreated(payment.getBundleId().toString(), payment.getAccountId().toString(), orderCode);
            } else if (payment.getAddonId() != null) {
                paymentProducer.sendAddonCreated(payment.getAddonId().toString(), payment.getAccountId().toString(), orderCode);
            } else if (payment.getSubscriptionId() != null) {
                paymentProducer.sendSubscriptionCreated(payment.getSubscriptionId().toString(), payment.getAccountId().toString(), orderCode);
            }

        } else {
            payment.setStatus(3); // FAILED/CANCELLED
        }

        // 3. Lưu mô tả lỗi/thành công để debug
        payment.setNote(webhookData.getDesc());
        payment.setLastUpdated(LocalDateTime.now());

        paymentRepository.save(payment);
    }

}
