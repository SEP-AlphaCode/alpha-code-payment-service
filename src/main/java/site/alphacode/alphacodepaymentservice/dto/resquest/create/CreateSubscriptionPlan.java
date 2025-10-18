package site.alphacode.alphacodepaymentservice.dto.resquest.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSubscriptionPlan {

    @NotBlank(message = "Tên gói không được để trống")
    @Size(max = 255, message = "Tên gói tối đa 255 ký tự")
    private String name;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String description;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private Integer price;

    @NotNull(message = "Billing cycle không được để trống")
    @Min(value = 1, message = "Billing cycle phải ít nhất 1 tháng")
    private Integer billingCycle; // in months
}
