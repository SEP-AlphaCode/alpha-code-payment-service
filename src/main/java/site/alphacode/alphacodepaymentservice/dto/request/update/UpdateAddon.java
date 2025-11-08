package site.alphacode.alphacodepaymentservice.dto.request.update;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodepaymentservice.base.BaseEntityDto;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateAddon extends BaseEntityDto {
    @NotNull(message = "Id addon là bắt buộc")
    private UUID id;

    @NotBlank(message = "Tên add-on không được để trống")
    @Size(max = 100, message = "Tên add-on không được vượt quá 100 ký tự")
    private String name;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private Integer price;

    @NotNull(message = "Danh mục không được để trống")
    private Integer category;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String description;

}
