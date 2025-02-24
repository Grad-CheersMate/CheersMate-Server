package CheersMate.cheersmate.domain.dto;

import lombok.Data;

@Data
public class CommunityCommentRequestDto {
    private Long communityId;
    private String content;
}
