package site.alphacode.alphacodepaymentservice.grpc.server;

import io.grpc.Status;
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
        GetLicenseResponse.Builder responseBuilder = GetLicenseResponse.newBuilder();
        try {
            UUID accountId = UUID.fromString(request.getAccountId());

            var licenseKey = licenseKeyService.getLicenseByAccountId(accountId);

            if (licenseKey == null) {
                responseBuilder.setHasLicense(false);
            } else {
                responseBuilder
                        .setHasLicense(true)
                        .setKey(licenseKey.getKey())
                        .setStatus(licenseKey.getStatus());
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid accountId format: " + e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal error: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}
