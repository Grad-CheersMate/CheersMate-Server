package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.*;
import CheersMate.cheersmate.domain.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<FrontendRecommendationResponse> getRecommendation(@RequestBody RecommendationRequest request) {
        FrontendRecommendationResponse response = recommendationService.getFrontendRecommendation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recommend/evaluate")
    public String evaluateRecommendation(@RequestBody FeedbackRequest feedbackRequest) {
        recommendationService.saveFeedback(feedbackRequest);
        return "사용자 평가가 성공적으로 저장되었습니다.";
    }

    @GetMapping("/recommend/weather")
    public ResponseEntity<FrontendRecommendationResponse> getWeatherRecommendation() {
        FrontendRecommendationResponse response = recommendationService.getWeatherRecommendation();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/feedback")
    public ResponseEntity<FeedbackStatisticsDTO> getFeedbackStatistics() {
        FeedbackStatisticsDTO statistics = recommendationService.getFeedbackStatistics();
        return ResponseEntity.ok(statistics);
    }
}
