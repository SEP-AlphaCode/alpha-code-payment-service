package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.request.create.CreatePayment;
import vn.payos.type.CheckoutResponseData;

public interface PaymentService {
    CheckoutResponseData createPayOSEmbeddedLink(CreatePayment createPayment) throws Exception;
}
