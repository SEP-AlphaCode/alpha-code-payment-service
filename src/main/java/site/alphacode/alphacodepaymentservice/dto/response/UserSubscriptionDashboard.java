package site.alphacode.alphacodepaymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDashboard {
    private String planName;
    private LocalDateTime endDate;
    private Integer status;
}
