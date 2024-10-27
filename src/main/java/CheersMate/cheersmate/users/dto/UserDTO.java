package CheersMate.cheersmate.users.dto;

import CheersMate.cheersmate.users.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String nickname;
    private String password;
    private String email;
    private String phone;
    private Role role;
}