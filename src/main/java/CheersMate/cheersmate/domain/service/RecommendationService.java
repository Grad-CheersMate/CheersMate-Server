package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.*;
import CheersMate.cheersmate.domain.entity.Feedback;
import CheersMate.cheersmate.domain.entity.Liquor;
import CheersMate.cheersmate.domain.enums.Companion;
import CheersMate.cheersmate.domain.enums.Emotion;
import CheersMate.cheersmate.domain.enums.Volume;
import CheersMate.cheersmate.domain.repository.FeedbackRepository;
import CheersMate.cheersmate.domain.repository.LiquorRepository;
import CheersMate.cheersmate.weather.entity.WeatherData;
import CheersMate.cheersmate.weather.repository.WeatherDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // Flask 응답을 프론트엔드 응답으로 변환
    public FrontendRecommendationResponse getFrontendRecommendation(RecommendationRequest request) {
        RecommendationResponse flaskResponse = getRecommendation(request);

        FrontendRecommendationResponse frontendResponse = transformToFrontendResponse(flaskResponse);

        return frontendResponse;
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
        int volumeCode = Volume.fromString(request.getVolume()).getCode();

        // Flask 서버로 보낼 요청 데이터 구성
        RecommendationRequestWithCondition flaskRequest = new RecommendationRequestWithCondition();
        flaskRequest.setCondition(condition);
        flaskRequest.setEmotion(emotionCode);
        flaskRequest.setCompanion(companionCode);
        flaskRequest.setVolume(volumeCode);


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

    // WeatherRecommendation 메서드 추가
    public FrontendRecommendationResponse getWeatherRecommendation() {
        WeatherRecommendationResponse flaskResponse = getWeatherBasedRecommendation();

        FrontendRecommendationResponse frontendResponse = transformToFrontendWeatherResponse(flaskResponse);

        return frontendResponse;
    }

    // Flask의 /recommend/weather API 호출 메서드
    public WeatherRecommendationResponse getWeatherBasedRecommendation() {
        // 최신 날씨 데이터 가져오기
        WeatherData latestWeatherData = weatherDataRepository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
        if (latestWeatherData == null) {
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }

        // 날씨 상태를 condition 값으로 변환
        int condition = mapWeatherConditionToInt(latestWeatherData.getWeatherCondition());

        String url = FLASK_SERVER_URL + "/recommend/weather?condition=" + condition;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<WeatherRecommendationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                WeatherRecommendationResponse.class
        );

        if (response.getBody() == null || !response.getBody().isResult()) {
            String errorMessage = response.getBody() != null ? response.getBody().getError() : "Flask 서버로부터 응답을 받지 못했습니다.";
            throw new RuntimeException(errorMessage);
        }

        return response.getBody();
    }

    // Flask 응답을 프론트엔드 응답으로 변환하는 메서드 (Weather)
    private FrontendRecommendationResponse transformToFrontendWeatherResponse(WeatherRecommendationResponse flaskResponse) {
        FrontendRecommendationResponse frontendResponse = new FrontendRecommendationResponse();

        // 기본 필드 설정
        frontendResponse.setResult(flaskResponse.isResult());
        frontendResponse.setHttpCode(flaskResponse.getHttpCode());
        frontendResponse.setError(flaskResponse.getError());

        // Data 객체 변환
        FrontendRecommendationResponse.Data frontendData = new FrontendRecommendationResponse.Data();

        // Request 필드 복사
        FrontendRecommendationResponse.Request frontendRequest = new FrontendRecommendationResponse.Request();
        frontendRequest.setWeather(flaskResponse.getData().getRequest().getWeather());
        frontendData.setRequest(frontendRequest);

        // Recommend 리스트 변환
        List<FrontendRecommendationResponse.Recommend> frontendRecommendList = new ArrayList<>();
        for (WeatherRecommendationResponse.Recommend flaskRecommend : flaskResponse.getData().getRecommend()) {
            FrontendRecommendationResponse.Recommend frontendRecommend = new FrontendRecommendationResponse.Recommend();
            FrontendRecommendationResponse.Liquor frontendLiquor = new FrontendRecommendationResponse.Liquor();

            frontendLiquor.setName(flaskRecommend.getName());
            frontendLiquor.setVolume(flaskRecommend.getVolume());
            frontendLiquor.setType(flaskRecommend.getType());
            frontendLiquor.setImageUrl(flaskRecommend.getImageUrl());

            frontendRecommend.setLiquor(frontendLiquor);
            frontendRecommendList.add(frontendRecommend);
        }
        frontendData.setRecommend(frontendRecommendList);

        // food와 similar 필드는 없음
        frontendData.setFood(null);
        frontendData.setSimilar(null);

        frontendResponse.setData(frontendData);

        return frontendResponse;
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

    private FrontendRecommendationResponse transformToFrontendResponse(RecommendationResponse flaskResponse) {
        FrontendRecommendationResponse frontendResponse = new FrontendRecommendationResponse();

        // 기본 필드 설정
        frontendResponse.setResult(flaskResponse.isResult());
        frontendResponse.setHttpCode(flaskResponse.getHttpCode());
        frontendResponse.setError(flaskResponse.getError());

        // Data 객체 변환
        FrontendRecommendationResponse.Data frontendData = new FrontendRecommendationResponse.Data();

        // Request 필드 복사
        FrontendRecommendationResponse.Request frontendRequest = new FrontendRecommendationResponse.Request();
        frontendRequest.setWeather(flaskResponse.getData().getRequest().getWeather());
        frontendRequest.setEmotion(flaskResponse.getData().getRequest().getEmotion());
        frontendRequest.setCompanion(flaskResponse.getData().getRequest().getCompanion());
        frontendData.setRequest(frontendRequest);

        // Recommend 변환 (단일 객체를 리스트로 변환)
        FrontendRecommendationResponse.Recommend frontendRecommend = new FrontendRecommendationResponse.Recommend();
        FrontendRecommendationResponse.Liquor frontendLiquor = new FrontendRecommendationResponse.Liquor();
        RecommendationResponse.Recommend flaskRecommend = flaskResponse.getData().getRecommend();

        frontendLiquor.setName(flaskRecommend.getName());
        frontendLiquor.setVolume(flaskRecommend.getVolume());
        frontendLiquor.setType(flaskRecommend.getType());
        frontendLiquor.setImageUrl(flaskRecommend.getImageUrl());
        frontendRecommend.setLiquor(frontendLiquor);

        List<FrontendRecommendationResponse.Recommend> frontendRecommendList = new ArrayList<>();
        frontendRecommendList.add(frontendRecommend);
        frontendData.setRecommend(frontendRecommendList);

        // Food 복사
        List<FrontendRecommendationResponse.Food> frontendFoodList = new ArrayList<>();
        for (RecommendationResponse.Food flaskFood : flaskResponse.getData().getFood()) {
            FrontendRecommendationResponse.Food frontendFood = new FrontendRecommendationResponse.Food();
            frontendFood.setName(flaskFood.getName());
            frontendFood.setImageUrl(flaskFood.getImageUrl());
            frontendFoodList.add(frontendFood);
        }
        frontendData.setFood(frontendFoodList);

        // Similar 리스트 변환
        List<FrontendRecommendationResponse.SimilarItem> frontendSimilarList = new ArrayList<>();
        for (RecommendationResponse.SimilarItem flaskSimilarItem : flaskResponse.getData().getSimilar()) {
            FrontendRecommendationResponse.SimilarItem frontendSimilarItem = new FrontendRecommendationResponse.SimilarItem();
            FrontendRecommendationResponse.Liquor similarLiquor = new FrontendRecommendationResponse.Liquor();

            similarLiquor.setName(flaskSimilarItem.getName());
            similarLiquor.setImageUrl(flaskSimilarItem.getImageUrl());
            // 필요한 경우 다른 필드도 설정

            frontendSimilarItem.setLiquor(similarLiquor);
            frontendSimilarList.add(frontendSimilarItem);
        }
        frontendData.setSimilar(frontendSimilarList);

        frontendResponse.setData(frontendData);

        return frontendResponse;
    }

    // WeatherCondition을 condition(int)로 매핑하는 메서드
    private int mapWeatherConditionToInt(String weatherCondition) {
        // weatherCondition에 따라 적절한 int 값을 반환하도록 매핑
        switch (weatherCondition) {
            case "sunny":
                return 0;
            case "rainy":
                return 1;
            case "snowy":
                return 2;
            case "cloudy":
                return 3;
            case "hot":
                return 4;
            case "windy":
                return 5;
            case "cold":
                return 6;
            default:
                return 0; // 기본값 설정
        }
    }

    public FeedbackStatisticsDTO getFeedbackStatistics() {
        List<Feedback> feedbacks = feedbackRepository.findAll();

        // 감정별 평균 점수 계산
        Map<String, Double> emotionStats = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        feedback -> convertEmotion(feedback.getEmotion()), // 감정을 사람이 읽을 수 있는 값으로 변환
                        Collectors.averagingDouble(Feedback::getRating)   // 각 그룹의 평균 점수 계산
                ));

        // 날씨별 평균 점수 계산
        Map<String, Double> weatherStats = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        feedback -> convertWeatherCondition(feedback.getWeatherCondition()), // 날씨를 사람이 읽을 수 있는 값으로 변환
                        Collectors.averagingDouble(Feedback::getRating)                      // 각 그룹의 평균 점수 계산
                ));

        // 동반자별 평균 점수 계산
        Map<String, Double> companionStats = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        feedback -> convertCompanion(feedback.getCompanion()), // 동반자를 사람이 읽을 수 있는 값으로 변환
                        Collectors.averagingDouble(Feedback::getRating)       // 각 그룹의 평균 점수 계산
                ));

        // Calculate average rating
        double averageRating = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        // Return as DTO
        return new FeedbackStatisticsDTO(averageRating, weatherStats, emotionStats, companionStats);
    }

    private String convertWeatherCondition(int code) {
        switch (code) {
            case 0: return "맑음";
            case 1: return "비";
            case 2: return "눈";
            case 3: return "흐림";
            case 4: return "더운 날";
            case 5: return "추운 날";
            case 6: return "바람 부는 날";
            default: return "알 수 없음";
        }
    }

    private String convertEmotion(int code) {
        for (Emotion emotion : Emotion.values()) {
            if (emotion.getCode() == code) {
                return emotion.name();
            }
        }
        return "알 수 없음";
    }

    private String convertCompanion(int code) {
        for (Companion companion : Companion.values()) {
            if (companion.getCode() == code) {
                return companion.name();
            }
        }
        return "알 수 없음";
    }

    public List<RatingDTO> getTopRatedLiquors() {
        Pageable top20 = PageRequest.of(0, 20); // 상위 20개 페이징
        List<Object[]> results = feedbackRepository.findTopRatedLiquors(top20);

        // Object[] 데이터를 LiquorDTO로 변환하고 RatingDTO로 감싸기
        return results.stream()
                .map(row -> {
                    LiquorDTO liquor = new LiquorDTO();
                    liquor.setId((Long) row[0]);          // liquorId
                    liquor.setName((String) row[1]);      // liquorName
                    liquor.setImageLink((String) row[2]); // liquorImage
                    return new RatingDTO(liquor); // LiquorDTO를 감싸기
                })
                .collect(Collectors.toList());
    }
}
