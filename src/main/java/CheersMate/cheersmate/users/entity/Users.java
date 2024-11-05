package CheersMate.cheersmate.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Long userId;

    @Column(name = "Nickname", unique = true, nullable = false)
    @NotEmpty(message = "Nickname is required")
    private String nickname;

    @Column(name = "Password", nullable = false)
    @NotEmpty(message = "Password is required")
    private String password;

    @Column(name = "Email", unique = true, nullable = false)
    @NotEmpty(message = "Email is required")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message = "이메일 주소 양식을 확인해주세요")
    private String email;

    @Column(name = "Tell", nullable = false)
    @NotEmpty(message = "Tell is required")
    private String tell;

    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false)
    private Role role;
}