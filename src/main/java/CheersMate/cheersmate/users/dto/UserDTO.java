package CheersMate.cheersmate.users.dto;

import CheersMate.cheersmate.users.entity.Role;
import CheersMate.cheersmate.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String nickname;
    private String password;
    private String email;
    private String tell;
    private Role role;

    // User Entity를 UserDTO로 변환하는 메서드
    public static UserDTO fromEntity(Users user) {
        return UserDTO.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .tell(user.getTell())
                .role(user.getRole())
                .build();
    }
}