package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.*;
import CheersMate.cheersmate.domain.entity.Feedback;
import CheersMate.cheersmate.domain.entity.Liquor;
import CheersMate.cheersmate.domain.enums.Companion;
import CheersMate.cheersmate.domain.enums.Emotion;
import CheersMate.cheersmate.domain.repository.FeedbackRepository;
import CheersMate.cheersmate.domain.repository.LiquorRepository;
import CheersMate.cheersmate.weather.entity.WeatherData;
import CheersMate.cheersmate.weather.repository.WeatherDataRepository;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate;
    private final FeedbackRepository feedbackRepository;
    private final WeatherDataRepository weatherDataRepository;
    private final LiquorRepository liquorRepository;

    // Flask 서버 AWS로 설정
    private final String FLASK_SERVER_URL = "http://52.79.37.145:5001";

    public RecommendationService(RestTemplate restTemplate, FeedbackRepository feedbackRepository, WeatherDataRepository weatherDataRepository, LiquorRepository liquorRepository) {
        this.restTemplate = restTemplate;
        this.feedbackRepository = feedbackRepository;
        this.weatherDataRepository = weatherDataRepository;
        this.liquorRepository = liquorRepository;

        // RestTemplate에 UTF-8 인코딩 설정 추가
        this.restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public RecommendationResponse getRecommendation(RecommendationRequest request) {
        // 최신 날씨 데이터 가져오기
        WeatherData latestWeatherData = weatherDataRepository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
        if (latestWeatherData == null) {
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }

        // 날씨 상태를 condition 값으로 변환
        int condition = mapWeatherConditionToInt(latestWeatherData.getWeatherCondition());

        // 문자열 입력을 Enum을 통해 숫자 코드로 변환
        int emotionCode = Emotion.fromString(request.getEmotion()).getCode();
        int companionCode = Companion.fromString(request.getCompanion()).getCode();

        // Flask 서버로 보낼 요청 데이터 구성
        RecommendationRequestWithCondition flaskRequest = new RecommendationRequestWithCondition();
        flaskRequest.setCondition(condition);
        flaskRequest.setEmotion(emotionCode);
        flaskRequest.setCompanion(companionCode);


        String url = FLASK_SERVER_URL + "/recommend";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RecommendationRequestWithCondition> httpEntity = new HttpEntity<>(flaskRequest, headers);
        ResponseEntity<RecommendationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                RecommendationResponse.class
        );

        if (response.getBody() == null || !response.getBody().isResult()) {
            String errorMessage = response.getBody() != null ? response.getBody().getError() : "Flask 서버로부터 응답을 받지 못했습니다.";
            throw new RuntimeException(errorMessage);
        }

        return response.getBody();
    }

    public void saveFeedback(FeedbackRequest feedbackRequest) {
        // 최신 날씨 데이터 가져오기
        WeatherData latestWeatherData = weatherDataRepository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
        if (latestWeatherData == null) {
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }

        // 날씨 상태를 weatherCondition(int) 값으로 변환
        int weatherCondition = mapWeatherConditionToInt(latestWeatherData.getWeatherCondition());

        // 문자열 입력을 Enum을 통해 숫자 코드로 변환
        int emotionCode = Emotion.fromString(feedbackRequest.getEmotion()).getCode();
        int companionCode = Companion.fromString(feedbackRequest.getCompanion()).getCode();


        // Liquor 이름으로 Liquor 엔티티 조회
        Optional<Liquor> optionalLiquor = liquorRepository.findByName(feedbackRequest.getLiquor().getName());
        if (!optionalLiquor.isPresent()) {
            throw new RuntimeException("주류 '" + feedbackRequest.getLiquor().getName() + "'를 찾을 수 없습니다.");
        }
        Liquor liquor = optionalLiquor.get();

        // Feedback 생성 및 저장
        Feedback feedback = new Feedback(
                weatherCondition,
                emotionCode,
                companionCode,
                liquor,
                feedbackRequest.getRating()
        );

        feedbackRepository.save(feedback);
    }

    // WeatherCondition을 condition(int)로 매핑하는 메서드
    private int mapWeatherConditionToInt(String weatherCondition) {
        // weatherCondition에 따라 적절한 int 값을 반환하도록 매핑
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
                return 0; // 기본값 설정
        }
    }
}
