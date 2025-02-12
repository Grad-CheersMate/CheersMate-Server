package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.FeedbackRequest;
import CheersMate.cheersmate.domain.dto.FeedbackResponse;
import CheersMate.cheersmate.domain.dto.FeedbackStatisticsDTO;
import CheersMate.cheersmate.domain.dto.RatingDTO;
import CheersMate.cheersmate.domain.service.FeedbackService;
import CheersMate.cheersmate.response.RatingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/recommend/evaluate")
    public ResponseEntity<FeedbackResponse> saveFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        feedbackService.saveFeedback(feedbackRequest);
        FeedbackResponse response = new FeedbackResponse(true, 200, "feedback save");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/feedback")
    public ResponseEntity<FeedbackStatisticsDTO> getFeedbackStatistics() {
        FeedbackStatisticsDTO stats = feedbackService.getFeedbackStatistics();
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/recommend/rating")
    public ResponseEntity<?> getTopRatedLiquors() {
        List<RatingDTO> ratings = feedbackService.getTopRatedLiquors();
        log.info("{\"result\": 1, \"httpCode\": 200, \"data\": {}}", ratings);
        return ResponseEntity.ok(new RatingResponse(true, 200, ratings));
    }
}
