package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.*;
import CheersMate.cheersmate.domain.service.RecommendationService;
import CheersMate.cheersmate.response.RatingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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


    @GetMapping("/recommend/weather")
    public ResponseEntity<FrontendRecommendationResponse> getWeatherRecommendation() {
        FrontendRecommendationResponse response = recommendationService.getWeatherRecommendation();
        return ResponseEntity.ok(response);
    }

}
