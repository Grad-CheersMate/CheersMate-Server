package CheersMate.cheersmate.response;

import CheersMate.cheersmate.users.dto.UserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
public class UserResponse extends ApiResponse {
    private UserDTO user;
    private Page<UserDTO> pageUsers;

    public UserResponse(boolean result, int httpCode, UserDTO user) {
        super(result, httpCode);
        this.user = user;
    }

    public UserResponse(boolean result, int httpCode, Page<UserDTO> pageUsers) {
        super(result, httpCode);
        this.pageUsers = pageUsers;
    }
}
