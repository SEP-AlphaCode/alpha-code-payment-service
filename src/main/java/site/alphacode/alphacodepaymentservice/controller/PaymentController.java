package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreatePayment;
import site.alphacode.alphacodepaymentservice.service.PayOSService;
import site.alphacode.alphacodepaymentservice.service.PaymentService;
import vn.payos.type.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;
    private final PayOSService payOSService;

    @PostMapping("/payos/verify-payment-webhook-data")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Received PayOS webhook: " + payload);

        try {
            // Lấy data từ payload
            Map<String, Object> dataMap = (Map<String, Object>) payload.get("data");

            // Tạo WebhookData
            WebhookData webhookData = WebhookData.builder()
                    .orderCode(Long.parseLong(dataMap.get("orderCode").toString()))
                    .amount(Integer.parseInt(dataMap.get("amount").toString()))
                    .description((String) dataMap.get("description"))
                    .accountNumber((String) dataMap.get("accountNumber"))
                    .reference((String) dataMap.get("reference"))
                    .transactionDateTime((String) dataMap.get("transactionDateTime"))
                    .currency((String) dataMap.get("currency"))
                    .paymentLinkId((String) dataMap.get("paymentLinkId"))
                    .code((String) dataMap.get("code"))
                    .desc((String) dataMap.get("desc"))
                    .counterAccountBankId((String) dataMap.get("counterAccountBankId"))
                    .counterAccountBankName((String) dataMap.get("counterAccountBankName"))
                    .counterAccountName((String) dataMap.get("counterAccountName"))
                    .counterAccountNumber((String) dataMap.get("counterAccountNumber"))
                    .virtualAccountName((String) dataMap.get("virtualAccountName"))
                    .virtualAccountNumber((String) dataMap.get("virtualAccountNumber"))
                    .build();

            // Xác định success dựa vào code hoặc desc
            Boolean success = "00".equals(payload.get("code")) ||
                    "success".equalsIgnoreCase((String) payload.get("desc"));

            // Tạo Webhook
            Webhook webhook = Webhook.builder()
                    .code((String) payload.get("code"))
                    .desc((String) payload.get("desc"))
                    .success(success)
                    .data(webhookData)
                    .signature((String) payload.get("signature"))
                    .build();

            // Gọi service xử lý
            payOSService.processWebhook(webhook);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @PostMapping("/github")
    public ResponseEntity<String> handleGitHubWebhook(@RequestBody Map<String, Object> payload,
                                                      @RequestHeader("X-GitHub-Event") String event,
                                                      @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {
        System.out.println("GitHub event: " + event);
        System.out.println("GitHub signature: " + signature);
        System.out.println("Payload: " + payload);

        // TODO: xử lý payload nếu muốn
        // Ví dụ chỉ log khi event = "push"
        if ("push".equals(event)) {
            System.out.println("Push event received!");
        }

        return ResponseEntity.ok("Received"); // trả 200 cho GitHub
    }

    @PostMapping("/payos/get-embedded-link")
    public CheckoutResponseData getEmbeddedLink(@RequestBody CreatePayment createPayment) throws Exception {
         return paymentService.createPayOSEmbeddedLink(createPayment);
    }

    @GetMapping("/payos/get-payment-link-information/{orderCode}")
    @Operation(summary = "Get PayOS payment link information by order code")
    public PaymentLinkData getPaymentLinkInformation(@PathVariable Long orderCode) throws Exception {
        return payOSService.getPaymentLinkInformation(orderCode);
    }

    @PutMapping("/payos/cancel-link-payment/{orderCode}")
    @Operation(summary = "Cancel PayOS payment link by order code")
    public PaymentLinkData cancelLinkPayment(@PathVariable Long orderCode, String cancelReason) throws Exception {
        return payOSService.cancelPaymentLink(orderCode, cancelReason);
    }

    @PostMapping("/payos/confirm-webhook")
    @Operation(summary = "Confirm PayOS webhook URL")
    public void confirmWebhook(String webhookUrl) throws Exception {
        payOSService.confirmWebhook(webhookUrl);
    }
}
