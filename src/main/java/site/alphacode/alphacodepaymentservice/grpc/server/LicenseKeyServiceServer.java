package site.alphacode.alphacodepaymentservice.grpc.server;

import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import license_key.GetLicenseRequest;
import license_key.GetLicenseResponse;
import license_key.LicenseKeyServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import site.alphacode.alphacodepaymentservice.enums.LicenseKeyEnum;
import site.alphacode.alphacodepaymentservice.service.LicenseKeyService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class LicenseKeyServiceServer extends LicenseKeyServiceGrpc.LicenseKeyServiceImplBase {
    private final LicenseKeyService licenseKeyService;

    @PostConstruct
    public void init() {
        log.info("LicenseKeyServiceGrpc initialized successfully");
    }

    @Override
    public void getLicenseByAccountId(GetLicenseRequest request, StreamObserver<GetLicenseResponse> responseObserver) {
        GetLicenseResponse.Builder responseBuilder;
        try {
            UUID accountId = UUID.fromString(request.getAccountId());

            var licenseKey = licenseKeyService.getLicenseByAccountId(accountId);
            responseBuilder = GetLicenseResponse.newBuilder();

            responseBuilder
                    .setHasLicense(true)
                    .setKey(licenseKey.getKey())
                    .setStatus(licenseKey.getStatus())  ;

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();

    } catch (IllegalArgumentException e) {
        // Lỗi UUID không hợp lệ
        responseObserver.onError(
                new RuntimeException("Invalid accountId format: " + e.getMessage())
        );
    } catch (Exception e) {
        // Bắt lỗi khác (VD: DB, service,…)
        responseObserver.onError(e);
    }


    }
}
