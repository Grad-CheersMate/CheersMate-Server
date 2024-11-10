package CheersMate.cheersmate.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private int result;
    private int resultCode;
    private String email;
    private String accessToken;
    private String refreshToken;

    public ApiResponse(int result, int resultCode) {
        this.result = result;
        this.resultCode = resultCode;
    }

    public ApiResponse(int result, int resultCode, String email) {
        this.result = result;
        this.resultCode = resultCode;
        this.email = email;
    }

    public ApiResponse(int result, int resultCode, String accessToken, String refreshToken) {
        this.result = result;
        this.resultCode = resultCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
