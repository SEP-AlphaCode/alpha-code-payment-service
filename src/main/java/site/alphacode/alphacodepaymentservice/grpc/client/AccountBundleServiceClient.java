package site.alphacode.alphacodepaymentservice.grpc.client;

import account_bundle.AccountBundle;
import account_bundle.AccountBundleServiceGrpc;
import account_course.AccountCourse;
import account_course.AccountCourseServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AccountBundleServiceClient {
    @GrpcClient("alpha-course-service")
    private AccountBundleServiceGrpc.AccountBundleServiceBlockingStub blockingStub;

    public boolean hasOwnedBundle(UUID accountId, UUID bundleId) {
        try {
            AccountBundle.CheckOwnBundleRequest request = AccountBundle.CheckOwnBundleRequest.newBuilder()
                    .setAccountId(accountId.toString())
                    .setBundleId(bundleId.toString())
                    .build();

            AccountBundle.CheckOwnBundleResponse response = blockingStub.checkOwnBundle(request);
            return response.getOwned();
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra sở hữu gói học qua gRPC: ", e);
            // Để tránh crash, bạn có thể trả về false hoặc ném exception tùy yêu cầu
            return false;
        }
    }
}
