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
    public ResponseEntity<FeedbackResponse> evaluateRecommendation(@RequestBody FeedbackRequest feedbackRequest) {
        recommendationService.saveFeedback(feedbackRequest);

        // 응답 객체 생성
        FeedbackResponse response = new FeedbackResponse(true, 200, "feedback save");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommend/weather")
    public ResponseEntity<FrontendRecommendationResponse> getWeatherRecommendation() {
        FrontendRecommendationResponse response = recommendationService.getWeatherRecommendation();
        return ResponseEntity.ok(response);
    }
}
