package CheersMate.cheersmate.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class FeedbackStatisticsDTO {
    private double averageRating; // 평균 평점
    private Map<String, Long> weatherStats; // 날씨 조건 통계
    private Map<String, Long> emotionStats; // 감정 통계
    private Map<String, Long> companionStats; // 동반자 통계
}
