package CheersMate.cheersmate.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse extends ApiResponse {
    private String message;

    public ErrorResponse(boolean result, int httpCode, String message) {
        super(result, httpCode);
        this.message = message;
    }
}
