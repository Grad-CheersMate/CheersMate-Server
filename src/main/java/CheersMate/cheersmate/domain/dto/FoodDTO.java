package CheersMate.cheersmate.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodDTO {
    private Long id;
    private String name;
    private String image;
    private String category;
}
