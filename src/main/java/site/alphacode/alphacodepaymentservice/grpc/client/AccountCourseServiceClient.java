package site.alphacode.alphacodepaymentservice.grpc.client;

import account_course.AccountCourse;
import account_course.AccountCourseServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AccountCourseServiceClient {

    @GrpcClient("alpha-course-service")
    private AccountCourseServiceGrpc.AccountCourseServiceBlockingStub blockingStub;

    /**
     * Kiểm tra xem người dùng đã sở hữu một khóa học cụ thể chưa.
     *
     * @param accountId ID của tài khoản người dùng
     * @param courseId  ID của khóa học
     * @return true nếu đã sở hữu, false nếu chưa
     */
    public boolean hasOwnedCourse(UUID accountId, UUID courseId) {
        try {
            AccountCourse.CheckOwnCourseRequest request = AccountCourse.CheckOwnCourseRequest.newBuilder()
                    .setAccountId(accountId.toString())
                    .setCourseId(courseId.toString())
                    .build();

            AccountCourse.CheckOwnCourseResponse response = blockingStub.checkOwnCourse(request);
            return response.getOwned();
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra sở hữu khóa học qua gRPC: ", e);
            // Để tránh crash, bạn có thể trả về false hoặc ném exception tùy yêu cầu
            return false;
        }
    }

    /**
     * Kiểm tra danh sách khóa học trong bundle xem người dùng đã sở hữu khóa nào chưa.
     *
     * @param accountId  ID của tài khoản người dùng
     * @param courseIds  Danh sách ID khóa học trong bundle
     * @return Danh sách ID khóa học mà user đã sở hữu
     */
    public List<UUID> getOwnedCoursesInBundle(UUID accountId, List<UUID> courseIds) {
        try {
            AccountCourse.CheckOwnCoursesInBundleRequest request = AccountCourse.CheckOwnCoursesInBundleRequest.newBuilder()
                    .setAccountId(accountId.toString())
                    .addAllCourseIds(courseIds.stream().map(UUID::toString).toList())
                    .build();

            AccountCourse.CheckOwnCoursesInBundleResponse response = blockingStub.checkOwnCoursesInBundle(request);

            return response.getOwnedCourseIdsList().stream()
                    .map(UUID::fromString)
                    .toList();
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra khóa học trong bundle qua gRPC: ", e);
            return List.of();
        }
    }
}
