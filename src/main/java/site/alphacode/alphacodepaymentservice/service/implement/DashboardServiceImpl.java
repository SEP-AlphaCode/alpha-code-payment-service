package site.alphacode.alphacodepaymentservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodepaymentservice.dto.response.Revenue;
import site.alphacode.alphacodepaymentservice.repository.PaymentRepository;
import site.alphacode.alphacodepaymentservice.service.DashboardService;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final RevenueRedisServiceImpl revenueRedisService;
    private final PaymentRepository paymentRepository;

    @Override
    public Revenue getRevenueByMonth(int month, int year) {

        Long currentRevenue =
                revenueRedisService.getRevenue(year, month);

        // fallback nếu redis chưa có
        if (currentRevenue == 0) {
            currentRevenue =
                    paymentRepository.sumRevenueByMonth(month, year);
        }

        int prevMonth = month - 1;
        int prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            prevYear--;
        }

        Long previousRevenue =
                revenueRedisService.getRevenue(prevYear, prevMonth);

        if (previousRevenue == 0) {
            previousRevenue =
                    paymentRepository.sumRevenueByMonth(prevMonth, prevYear);
        }

        double growth = 0;
        if (previousRevenue > 0) {
            growth = ((double) (currentRevenue - previousRevenue)
                    / previousRevenue) * 100;
        }

        return Revenue.builder()
                .month(month)
                .year(year)
                .currentRevenue(currentRevenue)
                .previousRevenue(previousRevenue)
                .growthPercent(Math.round(growth * 100.0) / 100.0)
                .build();
    }
}
