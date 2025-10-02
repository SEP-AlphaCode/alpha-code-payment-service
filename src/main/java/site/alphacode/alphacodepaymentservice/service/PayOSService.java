package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.resquest.create.PayOSEmbeddedLinkRequest;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.Webhook;

public interface PayOSService {
    CheckoutResponseData createEmbeddedLink(PayOSEmbeddedLinkRequest payOSEmbeddedLinkRequest,Long orderCode) throws Exception;
    void processWebhook(Webhook webhook) throws Exception;
}
