package CheersMate.cheersmate.domain.dto;

import CheersMate.cheersmate.domain.entity.Food;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodDTO {
    private Long id;
    private String name;
    private String imageLink;
    private String category;

    // Food Entity를 FoodDTO로 변환하는 메서드
    public static FoodDTO fromEntity(Food food) {
        return FoodDTO.builder()
                .id(food.getFoodId())
                .name(food.getName())
                .category(food.getCategory())
                .imageLink(food.getImageLink())
                .build();
    }
}
