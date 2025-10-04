package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.resquest.create.CreatePayment;
import vn.payos.type.CheckoutResponseData;

public interface PaymentService {
    CheckoutResponseData createPayOSEmbeddedLink(CreatePayment createPayment) throws Exception;
}
