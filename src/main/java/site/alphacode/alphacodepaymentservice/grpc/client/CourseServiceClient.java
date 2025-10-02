package site.alphacode.alphacodepaymentservice.grpc.client;

import course.Course;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import course.CourseServiceGrpc;


@Service
@Slf4j
public class CourseServiceClient {
    @GrpcClient("alpha-course-service")
    private CourseServiceGrpc.CourseServiceBlockingStub blockingStub;

    public Course.CourseInformation getCourseInformation(String courseId){
        log.info("getCourseInformation");

        Course.GetIdRequest request = Course.GetIdRequest.newBuilder().setId(courseId).build();

        try{
            Course.CourseInformation response = blockingStub.getCourse(request);
            log.info("getCourseInformation response={}",response);
            return response;
        }catch (StatusRuntimeException e) {
            log.error("gRPC call failed for courseId={}: {}", courseId, e.getStatus(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling UserService for courseId={}", courseId, e);
            throw e;
        }
    }

    public Course.BundleInformation getBundleInformation(String bundleId){
        log.info("getBundleInformation");
        Course.GetIdRequest request = Course.GetIdRequest.newBuilder().setId(bundleId).build();
        try{
            Course.BundleInformation response = blockingStub.getBundle(request);
            log.info("getBundleInformation response={}",response);
            return response;
        }catch (StatusRuntimeException e) {
            log.error("gRPC call failed for bundleId={}: {}", bundleId, e.getStatus(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling UserService for bundleId={}", bundleId, e);
            throw e;
        }
    }
}
