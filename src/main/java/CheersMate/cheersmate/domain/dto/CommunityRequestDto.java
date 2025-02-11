package CheersMate.cheersmate.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommunityRequestDto {
    // 작성자(user)의 ID가 필요합니다.
    private Long userId;

    @NotBlank(message = "Title is required")
    private String title;

    private String imageLink;
    private String description;
}
