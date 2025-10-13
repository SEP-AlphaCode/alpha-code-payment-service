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
import site.alphacode.alphacodepaymentservice.service.KeyPriceService;
import site.alphacode.alphacodepaymentservice.service.PayOSService;
import site.alphacode.alphacodepaymentservice.service.PaymentService;
import vn.payos.type.CheckoutResponseData;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImplement implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PayOSService payOSService;
    private final SubscriptionRepository subscriptionRepository;
    private final AddonRepository addonRepository;
    private final CourseServiceClient courseServiceClient;
    private final KeyPriceService keyPriceService;

    public CheckoutResponseData createPayOSEmbeddedLink(CreatePayment createPayment) throws Exception {

        // --- 1. Kiểm tra chỉ chọn 1 loại ---
        Map<String, Object> typeMap = new LinkedHashMap<>();
        typeMap.put("course", createPayment.getCourseId());
        typeMap.put("bundle", createPayment.getBundleId());
        typeMap.put("addon", createPayment.getAddonId());
        typeMap.put("subscription", createPayment.getSubscriptionId());
        typeMap.put("key", createPayment.getKeyId());

        List<String> selected = typeMap.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(Map.Entry::getKey)
                .toList();

        if (selected.size() != 1) {
            throw new IllegalArgumentException("Phải chọn đúng 1 loại dịch vụ: course, bundle, addon, key hoặc subscription");
        }

        String chosenType = selected.getFirst();
        String serviceName;
        int category;
        UUID serviceId; // để kiểm tra Payment pending
        int amount;

        switch (chosenType) {
            case "course":
                category = 1;
                serviceId = createPayment.getCourseId();
                var courseInfo = courseServiceClient.getCourseInformation(serviceId.toString());
                serviceName = courseInfo.getName().isEmpty() ? "Khóa học" : courseInfo.getName();
                amount = courseInfo.getPrice();
                break;
            case "bundle":
                category = 2;
                serviceId = createPayment.getBundleId();
                var bundleInfo = courseServiceClient.getBundleInformation(serviceId.toString());
                serviceName = bundleInfo.getName().isEmpty() ? "Gói học" : bundleInfo.getName();
                amount = bundleInfo.getPrice() - bundleInfo.getDiscountPrice();
                break;
            case "addon":
                category = 3;
                serviceId = createPayment.getAddonId();
                var addonInfo = addonRepository.findById(serviceId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy addon"));
                serviceName = addonInfo.getName();
                amount = addonInfo.getPrice();
                break;
            case "subscription":
                category = 4;
                serviceId = createPayment.getSubscriptionId();
                var subscriptionInfo = subscriptionRepository.findById(serviceId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy subscription"));
                serviceName = subscriptionInfo.getSubscriptionPlan().getName();
                amount = subscriptionInfo.getSubscriptionPlan().getPrice();
                break;
            case "key":
                category = 5;
                serviceId = createPayment.getKeyId();
                var keyPrice = keyPriceService.getKeyPrice();
                serviceName = "Mua license key";
                amount = keyPrice.getPrice();
                break;
            default:
                throw new IllegalArgumentException("Loại dịch vụ không hợp lệ");
        }

        // --- 1b. Kiểm tra Payment pending ---
        var existingPayment = paymentRepository
                .findFirstPendingByAccountAndService(createPayment.getAccountId(), category, serviceId,1);

        // --- 2. Tạo orderCode mới ---
        long orderCode = System.currentTimeMillis() / 1000;

        if (existingPayment.isPresent()) {
            //Hủy Payment cũ
            payOSService.cancelPaymentLink(existingPayment.get().getOrderCode(),"Hủy do tạo link mới");


            // Payment đang pending, trả về link cũ
            PayOSEmbeddedLinkRequest payRequest = new PayOSEmbeddedLinkRequest();
            payRequest.setPrice(amount);
            payRequest.setName(serviceName);

            var paymentUrl = payOSService.createEmbeddedLink(payRequest, orderCode);

            existingPayment.get().setOrderCode(paymentUrl.getOrderCode());
            existingPayment.get().setLastUpdated(LocalDateTime.now());
            existingPayment.get().setPaymentUrl(paymentUrl.getCheckoutUrl());
            existingPayment.get().setAmount(amount);

            paymentRepository.save(existingPayment.get());

            return paymentUrl;
        }

        // --- 3. Tạo Payment mới ---
        Payment payment = new Payment();
        payment.setOrderCode(orderCode);
        payment.setAmount(amount);
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

        // --- 4. Tạo request PayOS ---
        PayOSEmbeddedLinkRequest payRequest = new PayOSEmbeddedLinkRequest();
        payRequest.setPrice(amount);
        payRequest.setName(serviceName);

        var payData = payOSService.createEmbeddedLink(payRequest, orderCode);
        payment.setPaymentUrl(payData.getCheckoutUrl());
        paymentRepository.save(payment);

        // --- 5. Trả về dữ liệu PayOS ---
        return payData;
    }



}
