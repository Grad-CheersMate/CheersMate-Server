package CheersMate.cheersmate.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityLikeResponseDto {
    private Long likeId;
    private Long communityId;
    private Long userId;
    private LocalDateTime createdAt;
}
