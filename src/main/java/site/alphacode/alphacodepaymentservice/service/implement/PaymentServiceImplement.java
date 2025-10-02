package site.alphacode.alphacodepaymentservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreatePayment;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.PayOSEmbeddedLinkRequest;
import site.alphacode.alphacodepaymentservice.entity.Payment;
import site.alphacode.alphacodepaymentservice.grpc.client.CourseServiceClient;
import site.alphacode.alphacodepaymentservice.repository.AddonRepository;
import site.alphacode.alphacodepaymentservice.repository.PaymentRepository;
import site.alphacode.alphacodepaymentservice.repository.SubscriptionRepository;
import site.alphacode.alphacodepaymentservice.service.PayOSService;
import site.alphacode.alphacodepaymentservice.service.PaymentService;
import vn.payos.type.CheckoutResponseData;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImplement implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PayOSService payOSService;
    private final SubscriptionRepository subscriptionRepository;
    private final AddonRepository addonRepository;
    private final CourseServiceClient courseServiceClient;

    public CheckoutResponseData createPayOSEmbeddedLink(CreatePayment createPayment) throws Exception {
        // 1. Kiểm tra chỉ có đúng 1 loại dịch vụ được chọn
        int nonNullCount = 0;
        String serviceName = null;
        Integer category = null;

        if (createPayment.getCourseId() != null) {
            nonNullCount++;
            category = 1; // Course
            serviceName = courseServiceClient.getCourseInformation(createPayment.getCourseId().toString()).getName().isEmpty() ? "Khóa học" : courseServiceClient.getCourseInformation(createPayment.getCourseId().toString()).getName();
        }

        if (createPayment.getBundleId() != null) {
            nonNullCount++;
            category = 2; // Bundle
            serviceName = courseServiceClient.getBundleInformation(createPayment.getBundleId().toString()).getName().isEmpty() ? "Gói học" : courseServiceClient.getBundleInformation(createPayment.getBundleId().toString()).getName();
        }

        if (createPayment.getAddonId() != null) {
            nonNullCount++;
            category = 3; // Addon
            serviceName = addonRepository.findById(createPayment.getAddonId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy addon"))
                    .getName();
        }

        if (createPayment.getSubscriptionId() != null) {
            nonNullCount++;
            category = 4; // Subscription
            serviceName = subscriptionRepository.findById(createPayment.getSubscriptionId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy subscription"))
                    .getSubscriptionPlan().getName();
        }

        if (nonNullCount != 1) {
            throw new IllegalArgumentException("Phải chọn đúng 1 loại dịch vụ (subscription, addon, bundle hoặc course)");
        }

        // 2. Sinh orderCode (dùng làm khóa duy nhất với PayOS và DB)
        long orderCode = System.currentTimeMillis() / 1000;

        // 3. Tạo Payment trong DB với trạng thái "pending"
        Payment payment = new Payment();
        payment.setOrderCode(orderCode);
        payment.setAmount(createPayment.getAmount());
        payment.setCategory(category);
        payment.setPaymentMethod("PAYOS"); // hoặc mapping từ request
        payment.setStatus(1); // 1 = pending
        payment.setCreatedDate(LocalDateTime.now());
        payment.setLastUpdated(null);

        payment.setAccountId(createPayment.getAccountId());
        payment.setCourseId(createPayment.getCourseId());
        payment.setBundleId(createPayment.getBundleId());
        payment.setAddonId(createPayment.getAddonId());
        payment.setSubscriptionId(createPayment.getSubscriptionId());

        paymentRepository.save(payment);

        // 4. Tạo request cho PayOS
        PayOSEmbeddedLinkRequest payOSEmbeddedLinkRequest = new PayOSEmbeddedLinkRequest();
        payOSEmbeddedLinkRequest.setPrice(createPayment.getAmount());
        payOSEmbeddedLinkRequest.setName(serviceName);

        // 5. Gọi PayOS để lấy embedded link
        return payOSService.createEmbeddedLink(payOSEmbeddedLinkRequest, orderCode);
    }


}
