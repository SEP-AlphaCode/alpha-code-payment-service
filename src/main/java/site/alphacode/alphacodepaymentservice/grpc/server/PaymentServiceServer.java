package site.alphacode.alphacodepaymentservice.grpc.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import payment.GetKeyResponse;
import payment.GetRequest;
import payment.PaymentServiceGrpc;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyService;
import site.alphacode.alphacodepaymentservice.service.PaymentService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceServer extends PaymentServiceGrpc.PaymentServiceImplBase {
    private final LicenseKeyService licenseKeyService;

    @PostConstruct
    public void init() {
        log.info("PaymentServiceGrpc initialized successfully");
    }

    @Override
    public void getKeyByAccountId(GetRequest request, StreamObserver<GetKeyResponse> responseObserver) {
        try {
            UUID accountId = UUID.fromString(request.getId());
            var key = licenseKeyService.getKeyByAccountId(accountId);

            // Nếu không có key thì trả về response với field trống
            if (key == null) {
                log.info("Key not found, returning empty response");
                responseObserver.onNext(
                        GetKeyResponse.newBuilder()
                                .setKey("") // hoặc bỏ setKey nếu proto cho phép optional
                                .build()
                );
                responseObserver.onCompleted();
                return;
            }

            // Nếu có key thì trả bình thường
            responseObserver.onNext(
                    GetKeyResponse.newBuilder()
                            .setKey(key)
                            .build()
            );
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error in getKeyByAccountId: ", ex);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(ex)
                    .asRuntimeException());
        }
    }
}
