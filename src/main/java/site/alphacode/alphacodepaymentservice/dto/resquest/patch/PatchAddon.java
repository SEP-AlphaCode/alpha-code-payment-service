package site.alphacode.alphacodepaymentservice.dto.resquest.patch;

import jakarta.validation.constraints.Min;
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
public class PatchAddon extends BaseEntityDto {
    @Size(max = 100, message = "Tên add-on không được vượt quá 100 ký tự")
    private String name;

    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private Integer price;

    private Integer category;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String description;
}

