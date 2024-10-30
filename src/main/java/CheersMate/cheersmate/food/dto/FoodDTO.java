package CheersMate.cheersmate.food.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Long foodCategoryId;
}
