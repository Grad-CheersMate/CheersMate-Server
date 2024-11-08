package CheersMate.cheersmate.response;

import CheersMate.cheersmate.domain.dto.FoodDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FoodResponse extends ApiResponse {
    private FoodDTO food;
    private List<FoodDTO> foods;

    public FoodResponse(boolean result, int httpCode, FoodDTO food) {
        super(result, httpCode);
        this.food = food;
    }

    public FoodResponse(boolean result, int httpCode, List<FoodDTO> foods) {
        super(result, httpCode);
        this.foods = foods;
    }
}
