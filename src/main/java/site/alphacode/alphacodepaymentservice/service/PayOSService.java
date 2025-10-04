package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.resquest.create.PayOSEmbeddedLinkRequest;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

public interface PayOSService {
    CheckoutResponseData createEmbeddedLink(PayOSEmbeddedLinkRequest payOSEmbeddedLinkRequest,Long orderCode) throws Exception;
    WebhookData processWebhook(Webhook webhook) throws Exception;
    PaymentLinkData getPaymentLinkInformation(Long orderCode) throws Exception;
    PaymentLinkData cancelPaymentLink(Long orderCode, String cancelReason) throws Exception;
    String confirmWebhook(String webHookUrl) throws  Exception;
}
