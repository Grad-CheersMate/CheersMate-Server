package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.FeedbackRequest;
import CheersMate.cheersmate.domain.dto.RecommendationRequest;
import CheersMate.cheersmate.domain.dto.RecommendationResponse;
import CheersMate.cheersmate.domain.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommend")
    public RecommendationResponse getRecommendation(@RequestBody RecommendationRequest request) {
        return recommendationService.getRecommendation(request);
    }

    @PostMapping("/recommend/evaluate")
    public String evaluateRecommendation(@RequestBody FeedbackRequest feedbackRequest) {
        recommendationService.saveFeedback(feedbackRequest);
        return "사용자 평가가 성공적으로 저장되었습니다.";
    }
}
