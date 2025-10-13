package site.alphacode.alphacodepaymentservice.producer;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentProducer implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;

    @Override
    public void run(String... args) {
        System.out.println("RabbitMQ connected: " + rabbitTemplate.getConnectionFactory().getHost());
        rabbitAdmin.initialize();
    }

    public PaymentProducer(RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
    }

    // --- Course ---
    public void sendCourseCreated(String courseId, String accountId, Long orderCode) {
        Map<String, Object> message = Map.of(
                "courseId", courseId,
                "accountId", accountId,
                "orderCode", orderCode
        );
        rabbitTemplate.convertAndSend("payment.exchange", "course.create.queue", message);
    }

    // --- Bundle ---
    public void sendBundleCreated(String bundleId, String accountId, Long orderCode) {
        Map<String, Object> message = Map.of(
                "bundleId", bundleId,
                "accountId", accountId,
                "orderCode", orderCode
        );
        rabbitTemplate.convertAndSend("payment.exchange", "bundle.create.queue", message);
    }

    // --- Email ---
    public void sendEmail(String accountId, Long orderCode) {
        Map<String, Object> message = Map.of(
                "accountId", accountId,
                "orderCode", orderCode
        );
        rabbitTemplate.convertAndSend("payment.exchange", "email.send.queue", message);
    }
}
