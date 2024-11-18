package CheersMate.cheersmate.response;

import CheersMate.cheersmate.domain.dto.RatingDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RatingResponse extends ApiResponse {
    private List<RatingDTO> ratings;

    public RatingResponse(boolean result, int httpCode, List<RatingDTO> ratings) {
        super(result, httpCode);
        this.ratings = ratings;
    }
}
