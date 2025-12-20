package site.alphacode.alphacodepaymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DashboardRevenueResponse implements Serializable {
    private RevenueDto payment;
    private List<CategoryRevenueDto> category;
}
