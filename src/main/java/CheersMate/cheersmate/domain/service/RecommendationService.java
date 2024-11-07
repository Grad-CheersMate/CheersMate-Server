package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.FeedbackRequest;
import CheersMate.cheersmate.domain.dto.RecommendationRequest;
import CheersMate.cheersmate.domain.dto.RecommendationRequestWithCondition;
import CheersMate.cheersmate.domain.dto.RecommendationResponse;
import CheersMate.cheersmate.domain.entity.Feedback;
import CheersMate.cheersmate.domain.repository.FeedbackRepository;
import CheersMate.cheersmate.weather.entity.WeatherData;
import CheersMate.cheersmate.weather.repository.WeatherDataRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate;
    private final FeedbackRepository feedbackRepository;
    private final WeatherDataRepository weatherDataRepository;

    // Flask 서버의 로컬 URL로 변경
    private final String FLASK_SERVER_URL = "http://127.0.0.1:5001";

    public RecommendationService(RestTemplate restTemplate, FeedbackRepository feedbackRepository, WeatherDataRepository weatherDataRepository) {
        this.restTemplate = restTemplate;
        this.feedbackRepository = feedbackRepository;
        this.weatherDataRepository = weatherDataRepository;
    }

    public RecommendationResponse getRecommendation(RecommendationRequest request) {
        // 최신 날씨 데이터 가져오기
        WeatherData latestWeatherData = weatherDataRepository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
        if (latestWeatherData == null) {
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }

        // 날씨 상태를 condition 값으로 변환
        int condition = mapWeatherConditionToInt(latestWeatherData.getWeatherCondition());

        // Flask 서버로 보낼 요청 데이터 구성
        RecommendationRequestWithCondition flaskRequest = new RecommendationRequestWithCondition();
        flaskRequest.setCondition(condition);
        flaskRequest.setMood(request.getMood());
        flaskRequest.setCompanion(request.getCompanion());

        String url = FLASK_SERVER_URL + "/recommend";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RecommendationRequestWithCondition> httpEntity = new HttpEntity<>(flaskRequest, headers);
        ResponseEntity<RecommendationResponse> response = restTemplate.postForEntity(url, httpEntity, RecommendationResponse.class);

        return response.getBody();
    }

    public void saveFeedback(FeedbackRequest feedbackRequest) {
        Feedback feedback = new Feedback(
                feedbackRequest.getWeatherCondition(),
                feedbackRequest.getMood(),
                feedbackRequest.getCompanion(),
                feedbackRequest.getRecommendedLiquor(),
                feedbackRequest.getDrinkType(),
                feedbackRequest.getAlcoholContent(),
                feedbackRequest.getRating()
        );
        feedbackRepository.save(feedback);
    }

    // WeatherCondition을 condition(int)로 매핑하는 메서드
    private int mapWeatherConditionToInt(String weatherCondition) {
        // weatherCondition에 따라 적절한 int 값을 반환하도록 매핑합니다.
        switch (weatherCondition) {
            case "맑음":
                return 0;
            case "비":
                return 1;
            case "눈":
                return 2;
            case "흐림":
                return 3;
            case "더운 날":
                return 4;
            case "바람 부는 날":
                return 5;
            case "추운 날":
                return 6;
            default:
                return 0; // 기본값으로 맑음
        }
    }
}
