package site.alphacode.alphacodepaymentservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.CategoryRevenueDto;
import site.alphacode.alphacodepaymentservice.dto.response.DashboardRevenueResponse;
import site.alphacode.alphacodepaymentservice.dto.response.RevenueDto;
import site.alphacode.alphacodepaymentservice.enums.PaymentCategoryEnum;
import site.alphacode.alphacodepaymentservice.repository.PaymentRepository;
import site.alphacode.alphacodepaymentservice.service.DashboardService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RevenueRedisServiceImpl revenueRedisService;
    private final PaymentRepository paymentRepository;
    private final int revenueTtlSeconds = 60;
    private final Duration ttl = Duration.ofSeconds(revenueTtlSeconds);

    @Override
    public RevenueDto getRevenue(int year, Integer month, Integer day) {

        validateParams(month, day);

        long current;
        long previous;

        if (day != null) {
            // DAY
            current = getRevenueFromRedisOrDb(year, month, day);
            LocalDate prev = LocalDate.of(year, month, day).minusDays(1);
            previous = getRevenueFromRedisOrDb(prev.getYear(), prev.getMonthValue(), prev.getDayOfMonth());

        } else if (month != null) {
            // MONTH
            current = getRevenueFromRedisOrDb(year, month);
            YearMonth prev = YearMonth.of(year, month).minusMonths(1);
            previous = getRevenueFromRedisOrDb(prev.getYear(), prev.getMonthValue());

        } else {
            // YEAR
            current = getRevenueFromRedisOrDb(year);
            previous = getRevenueFromRedisOrDb(year - 1);
        }

        return RevenueDto.of(current, previous);
    }

    private void validateParams(Integer month, Integer day) {
        if (day != null && month == null) {
            throw new IllegalArgumentException("month is required when day is provided");
        }
    }

    /* ================= REDIS → DB FALLBACK ================= */

    private long getRevenueFromRedisOrDb(int year) {
        Long redis = revenueRedisService.getRevenue(year, 0);
        if (redis != null && redis > 0) return redis;

        long db = paymentRepository.sumRevenueByYear(year);
        revenueRedisService.setRevenueWithTTL(year, 0, db, ttl);
        return db;
    }

    private long getRevenueFromRedisOrDb(int year, int month) {
        Long redis = revenueRedisService.getRevenue(year, month);
        if (redis != null && redis > 0) return redis;

        long db = paymentRepository.sumRevenueByMonth(year, month);
        revenueRedisService.setRevenueWithTTL(year, month, db, ttl);
        return db;
    }

    private long getRevenueFromRedisOrDb(int year, int month, int day) {
        Long redis = revenueRedisService.getRevenue(year, month * 100 + day);
        if (redis != null && redis > 0) return redis;

        long db = paymentRepository.sumRevenueByDay(year, month, day);
//        revenueRedisService.setRevenue(year, month * 100 + day, db);
        revenueRedisService.setRevenueWithTTL(year, month * 100 + day, db, ttl);
        return db;
    }

    @Override
    public DashboardRevenueResponse getDashboardRevenue(int year, Integer month, Integer day) {

        RevenueDto revenue = getRevenue(year, month, day);

        List<CategoryRevenueDto> categories =
                getCategoryCount(year, month, day);

        return DashboardRevenueResponse.builder()
                .payment(revenue)
                .category(categories)
                .build();
    }

    private List<CategoryRevenueDto> getCategoryCount(
            int year, Integer month, Integer day
    ) {

        List<Object[]> raw;

        if (day != null) {
            validateParams(month, day);
            LocalDate date = LocalDate.of(year, month, day);
            raw = paymentRepository.countByCategoryDay(date);

        } else if (month != null) {
            raw = paymentRepository.countByCategoryMonth(year, month);

        } else {
            raw = paymentRepository.countByCategoryYear(year);
        }

        return raw.stream()
                .map(r -> {
                    int cateId = (int) r[0];
                    long count = (long) r[1];

                    return CategoryRevenueDto.builder()
                            .categoryId(cateId)
                            .categoryName(
                                    PaymentCategoryEnum.fromCode(cateId)
                            )
                            .amount(count)
                            .build();
                })
                .toList();
    }


}