package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.*;
import CheersMate.cheersmate.domain.entity.Feedback;
import CheersMate.cheersmate.domain.entity.Liquor;
import CheersMate.cheersmate.domain.enums.Companion;
import CheersMate.cheersmate.domain.enums.Emotion;
import CheersMate.cheersmate.domain.enums.Volume;
import CheersMate.cheersmate.domain.flask.FlaskRecommendationClient;
import CheersMate.cheersmate.domain.flask.RecommendationResponseTransformer;
import CheersMate.cheersmate.domain.repository.FeedbackRepository;
import CheersMate.cheersmate.domain.repository.LiquorRepository;
import CheersMate.cheersmate.domain.util.WeatherUtil;
import CheersMate.cheersmate.weather.entity.WeatherData;
import CheersMate.cheersmate.weather.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final WeatherDataRepository weatherDataRepository;
    private final FlaskRecommendationClient flaskClient;

    /**
     * 일반 추천: 사용자 입력 + 최신 날씨를 이용해 추천 요청 후 응답 변환
     */
    public FrontendRecommendationResponse getFrontendRecommendation(RecommendationRequest request) {
        // 최신 날씨 데이터 조회
        WeatherData latestWeather = weatherDataRepository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
        if (latestWeather == null) {
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }
        // 날씨 문자열 -> int 코드 변환
        int condition = WeatherUtil.mapWeatherConditionToInt(latestWeather.getWeatherCondition());
        int emotionCode = Emotion.fromString(request.getEmotion()).getCode();
        int companionCode = Companion.fromString(request.getCompanion()).getCode();
        int volumeCode = Volume.fromString(request.getVolume()).getCode();

        // RecommendationRequestWithCondition 구성
        RecommendationRequestWithCondition flaskRequest = new RecommendationRequestWithCondition();
        flaskRequest.setCondition(condition);
        flaskRequest.setEmotion(emotionCode);
        flaskRequest.setCompanion(companionCode);
        flaskRequest.setVolume(volumeCode);

        // Flask 서버로 추천 요청 (FlaskRecommendationClient 사용)
        RecommendationResponse flaskResponse = flaskClient.getRecommendation(flaskRequest);
        // 응답을 FrontendRecommendationResponse로 변환 (Transformer 사용)
        return RecommendationResponseTransformer.transform(flaskResponse);
    }

    /**
     * 날씨 추천: 최신 날씨 데이터를 기반으로 날씨 추천 요청 후 응답 변환
     */
    public FrontendRecommendationResponse getWeatherRecommendation() {
        WeatherData latestWeather = weatherDataRepository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
        if (latestWeather == null) {
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }
        int condition = WeatherUtil.mapWeatherConditionToInt(latestWeather.getWeatherCondition());
        WeatherRecommendationResponse flaskResponse = flaskClient.getWeatherRecommendation(condition);
        return RecommendationResponseTransformer.transformWeather(flaskResponse);
    }
}
