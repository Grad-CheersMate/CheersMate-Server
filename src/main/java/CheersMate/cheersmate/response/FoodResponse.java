package CheersMate.cheersmate.response;

import CheersMate.cheersmate.domain.dto.FoodDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class FoodResponse extends ApiResponse {
    private FoodDTO food;
    private List<FoodDTO> foods;
    private Page<FoodDTO> pageFoods;

    public FoodResponse(boolean result, int httpCode, FoodDTO food) {
        super(result, httpCode);
        this.food = food;
    }

    public FoodResponse(boolean result, int httpCode, List<FoodDTO> foods) {
        super(result, httpCode);
        this.foods = foods;
    }

    public FoodResponse(boolean result, int httpCode, Page<FoodDTO> pageFoods) {
        super(result, httpCode);
        this.pageFoods = pageFoods;
    }
}
