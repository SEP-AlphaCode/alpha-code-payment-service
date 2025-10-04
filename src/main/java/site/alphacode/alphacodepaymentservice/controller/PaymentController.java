package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreatePayment;
import site.alphacode.alphacodepaymentservice.entity.Payment;
import site.alphacode.alphacodepaymentservice.service.PayOSService;
import site.alphacode.alphacodepaymentservice.service.PaymentService;
import vn.payos.type.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;
    private final PayOSService payOSService;

    @PostMapping("/payos/verify-payment-webhook-data")
    @Operation(summary = "Handle PayOS webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody Webhook webhook) {
        try {
            payOSService.processWebhook(webhook);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    @GetMapping("/payos/confirm-webhook/{webhookUrl}")
    @Operation(summary = "Confirm PayOS webhook URL")
    public void confirmWebhook(@PathVariable String webhookUrl) throws Exception {
        payOSService.confirmWebhook(webhookUrl);
    }
}
