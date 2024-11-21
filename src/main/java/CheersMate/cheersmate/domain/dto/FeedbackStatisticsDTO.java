package CheersMate.cheersmate.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStatisticsDTO {

    private List<FeedbackCombinationDTO> combinations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackCombinationDTO {
        private String emotion;
        private String weatherCondition;
        private Long positiveCount;
        private Long negativeCount;
    }
}
