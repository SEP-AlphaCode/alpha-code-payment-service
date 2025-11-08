package site.alphacode.alphacodepaymentservice.dto.request.patch;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatchSubscriptionPlan {

    @Size(max = 255, message = "Tên gói tối đa 255 ký tự")
    private String name;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String description;

    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private Integer price;

    private Boolean isRecommended;

    @Min(value = 1, message = "Billing cycle phải ít nhất 1 tháng")
    private Integer billingCycle; // in months
}
