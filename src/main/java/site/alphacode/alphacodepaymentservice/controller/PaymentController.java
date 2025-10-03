package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreatePayment;
import site.alphacode.alphacodepaymentservice.service.PayOSService;
import site.alphacode.alphacodepaymentservice.service.PaymentService;
import vn.payos.type.*;

@RestController
@RequestMapping("/api/v1/license-keys")
@RequiredArgsConstructor
@Tag(name = "License Keys", description = "License Key management APIs")
public class PaymentController {

    private final PaymentService paymentService;
    private final PayOSService payOSService;

    @PostMapping("/payos/webhook")
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
}
