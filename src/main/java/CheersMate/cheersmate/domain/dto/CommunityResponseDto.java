package CheersMate.cheersmate.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityResponseDto {
    private Long communityId;
    private Long userId;
    private String title;
    private String imageLink;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
