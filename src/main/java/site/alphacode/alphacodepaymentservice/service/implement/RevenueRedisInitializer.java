package site.alphacode.alphacodepaymentservice.service.implement;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.alphacode.alphacodepaymentservice.repository.PaymentRepository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RevenueRedisInitializer {

    private final PaymentRepository paymentRepository;
    private final RevenueRedisServiceImpl revenueRedisService;

    @Value("${revenue.redis.ttl.seconds:3600}")
    private int revenueTtlSeconds;

    @PostConstruct
    public void syncRevenue() {

        List<Object[]> results = paymentRepository.sumRevenueGroupByMonth();
        Duration ttl = Duration.ofSeconds(revenueTtlSeconds);

        /*
         * Map to accumulate yearly revenue
         * key = year, value = total
         */
        Map<Integer, Long> yearlyRevenue = new HashMap<>();

        for (Object[] row : results) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long total = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            // SET month revenue (ABSOLUTE)
            revenueRedisService.setRevenueWithTTL(year, month, total, ttl);

            // accumulate year revenue
            yearlyRevenue.merge(year, total, Long::sum);
        }

        // SET year revenue
        for (var entry : yearlyRevenue.entrySet()) {
            revenueRedisService.setRevenueWithTTL(
                    entry.getKey(),
                    0, // month = 0 means YEAR
                    entry.getValue(),
                    ttl
            );
        }
    }
}
