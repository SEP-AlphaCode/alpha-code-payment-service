package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.PayOSEmbeddedLinkRequest;

import site.alphacode.alphacodepaymentservice.entity.Payment;
import site.alphacode.alphacodepaymentservice.repository.PaymentRepository;
import site.alphacode.alphacodepaymentservice.service.PayOSService;
import vn.payos.PayOS;
import vn.payos.type.*;

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

    final PayOS payOS = new PayOS(clientId, apiKey, checkSumKey);

    private final PaymentRepository paymentRepository;

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
        } else {
            payment.setStatus(3); // FAILED/CANCELLED
        }

        // 3. Lưu mô tả lỗi/thành công để debug
        payment.setNote(webhookData.getDesc()); // thêm field note vào entity nếu cần
        payment.setLastUpdated(LocalDateTime.now());

        paymentRepository.save(payment);
    }
}
