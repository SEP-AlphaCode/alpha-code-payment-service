package site.alphacode.alphacodepaymentservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RevenueDto implements Serializable {
    private long currentRevenue;
    private long previousRevenue;
    private double growthPercent;

    public static RevenueDto of(long current, long previous) {
        double growth = previous == 0
                ? (current > 0 ? 100 : 0)
                : ((double) (current - previous) / previous) * 100;

        return new RevenueDto(current, previous, growth);
    }
}
