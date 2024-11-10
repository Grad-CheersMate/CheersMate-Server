package CheersMate.cheersmate.response;

import CheersMate.cheersmate.domain.dto.LiquorDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
public class LiquorResponse extends ApiResponse{
    private LiquorDTO liquor;
    private Page<LiquorDTO> liquors;

    public LiquorResponse(boolean result, int httpCode, LiquorDTO liquor) {
        super(result, httpCode);
        this.liquor = liquor;
    }

    public LiquorResponse(boolean result, int httpCode, Page<LiquorDTO> liquors) {
        super(result, httpCode);
        this.liquors = liquors;
    }
}