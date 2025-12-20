package site.alphacode.alphacodepaymentservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Revenue {
    private Integer month;
    private Integer year;

    private Long currentRevenue;
    private Long previousRevenue;

    private Double growthPercent;
}
