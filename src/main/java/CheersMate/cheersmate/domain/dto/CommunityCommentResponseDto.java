package CheersMate.cheersmate.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityCommentResponseDto {
    private Long commentId;
    private Long communityId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
