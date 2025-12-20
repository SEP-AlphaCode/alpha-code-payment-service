package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.alphacode.alphacodepaymentservice.repository.PaymentRepository;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RevenueRedisInitializer {

    private final PaymentRepository paymentRepository;
    private final RevenueRedisServiceImpl revenueRedisService;

    @Value("${revenue.redis.ttl.seconds:3600}")
    private int revenueTtlSeconds;


    @PostConstruct
    public void syncRevenue() {

        List<Object[]> results =
                paymentRepository.sumRevenueGroupByMonth();

        Duration ttl = Duration.ofSeconds(revenueTtlSeconds);

        for (Object[] row : results) {
            int year = (int) row[0];
            int month = (int) row[1];
            Long total = (Long) row[2];

            // write absolute value into Redis and set TTL so keys expire if service dies
            revenueRedisService.setRevenueWithTTL(year, month, total != null ? total : 0L, ttl);
        }
    }
}
