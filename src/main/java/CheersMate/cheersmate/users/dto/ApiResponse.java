package CheersMate.cheersmate.users.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean result;
    private int httpCode;
    private T data;
    private String accessToken;
    private String refreshToken;

    public ApiResponse(boolean result, int httpCode) {
        this.result = result;
        this.httpCode = httpCode;
    }

    public ApiResponse(boolean result, int httpCode, T data) {
        this.result = result;
        this.httpCode = httpCode;
        this.data = data;
    }

    public ApiResponse(boolean result, int httpCode, String accessToken, String refreshToken) {
        this.result = result;
        this.httpCode = httpCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}