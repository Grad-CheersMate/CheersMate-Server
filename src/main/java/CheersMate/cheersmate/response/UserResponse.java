package CheersMate.cheersmate.response;

import CheersMate.cheersmate.users.dto.UserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserResponse extends ApiResponse {
    private UserDTO user;
    private List<UserDTO> users;

    public UserResponse(boolean result, int httpCode, UserDTO user) {
        super(result, httpCode);
        this.user = user;
    }

    public UserResponse(boolean result, int httpCode, List<UserDTO> users) {
        super(result, httpCode);
        this.users = users;
    }
}
