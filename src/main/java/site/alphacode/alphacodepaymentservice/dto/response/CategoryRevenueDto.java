package site.alphacode.alphacodepaymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class CategoryRevenueDto implements Serializable {
    private Integer categoryId;
    private String categoryName;
    private Long amount;
}
