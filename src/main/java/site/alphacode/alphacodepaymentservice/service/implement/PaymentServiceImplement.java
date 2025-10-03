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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImplement implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PayOSService payOSService;
    private final SubscriptionRepository subscriptionRepository;
    private final AddonRepository addonRepository;
    private final CourseServiceClient courseServiceClient;

    public CheckoutResponseData createPayOSEmbeddedLink(CreatePayment createPayment) throws Exception {

        // --- 1. Kiểm tra chỉ chọn 1 loại ---
        Map<String, Object> typeMap = new LinkedHashMap<>();
        typeMap.put("course", createPayment.getCourseId());
        typeMap.put("bundle", createPayment.getBundleId());
        typeMap.put("addon", createPayment.getAddonId());
        typeMap.put("subscription", createPayment.getSubscriptionId());

        List<String> selected = typeMap.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(Map.Entry::getKey)
                .toList();

        if (selected.size() != 1) {
            throw new IllegalArgumentException("Phải chọn đúng 1 loại dịch vụ: course, bundle, addon hoặc subscription");
        }

        String chosenType = selected.get(0);
        String serviceName;
        Integer category;

        switch (chosenType) {
            case "course":
                category = 1;
                var courseInfo = courseServiceClient.getCourseInformation(createPayment.getCourseId().toString());
                courseInfo.getName();
                serviceName = courseInfo.getName().isEmpty() ? "Khóa học" : courseInfo.getName();
                break;
            case "bundle":
                category = 2;
                var bundleInfo = courseServiceClient.getBundleInformation(createPayment.getBundleId().toString());
                bundleInfo.getName();
                serviceName = bundleInfo.getName().isEmpty() ? "Gói học" : bundleInfo.getName();
                break;
            case "addon":
                category = 3;
                serviceName = addonRepository.findById(createPayment.getAddonId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy addon"))
                        .getName();
                break;
            case "subscription":
                category = 4;
                serviceName = subscriptionRepository.findById(createPayment.getSubscriptionId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy subscription"))
                        .getSubscriptionPlan().getName();
                break;
            default:
                throw new IllegalStateException("Loại dịch vụ không hợp lệ");
        }

        // --- 2. Tạo orderCode ---
        long orderCode = System.currentTimeMillis() / 1000;

        // --- 3. Lưu Payment ---
        Payment payment = new Payment();
        payment.setOrderCode(orderCode);
        payment.setAmount(createPayment.getAmount());
        payment.setCategory(category);
        payment.setPaymentMethod("PAYOS");
        payment.setStatus(1); // pending
        payment.setCreatedDate(LocalDateTime.now());
        payment.setLastUpdated(null);

        payment.setAccountId(createPayment.getAccountId());
        payment.setCourseId(createPayment.getCourseId());
        payment.setBundleId(createPayment.getBundleId());
        payment.setAddonId(createPayment.getAddonId());
        payment.setSubscriptionId(createPayment.getSubscriptionId());

        paymentRepository.save(payment);

        // --- 4. Tạo request PayOS ---
        PayOSEmbeddedLinkRequest payRequest = new PayOSEmbeddedLinkRequest();
        payRequest.setPrice(createPayment.getAmount());
        payRequest.setName(serviceName);

        // --- 5. Gọi PayOS ---
        return payOSService.createEmbeddedLink(payRequest, orderCode);
    }



}
