package site.alphacode.alphacodepaymentservice.grpc.client;

import course_bundle.CourseBundleServiceGrpc;
import course_bundle.GetCourseIdsByBundleRequest;
import course_bundle.GetCourseIdsByBundleResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CourseBundleServiceClient {
    @GrpcClient("alpha-course-service")
    private CourseBundleServiceGrpc.CourseBundleServiceBlockingStub blockingStub;

    public List<UUID> getCourseIdsByBundleId(UUID bundleId) {
        try {
            GetCourseIdsByBundleRequest request = GetCourseIdsByBundleRequest.newBuilder()
                    .setBundleId(bundleId.toString())
                    .build();

            GetCourseIdsByBundleResponse response = blockingStub.getCourseIdsByBundle(request);

            return response.getCourseIdsList().stream()
                    .map(UUID::fromString)
                    .toList();
        } catch (Exception e) {
            log.error("Lỗi khi gọi gRPC CourseBundleService: ", e);
            return List.of();
        }
    }
}
