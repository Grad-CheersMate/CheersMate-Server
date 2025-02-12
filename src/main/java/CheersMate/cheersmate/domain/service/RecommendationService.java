package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.*;
import CheersMate.cheersmate.domain.entity.Feedback;
import CheersMate.cheersmate.domain.entity.Liquor;
import CheersMate.cheersmate.domain.enums.Companion;
import CheersMate.cheersmate.domain.enums.Emotion;
import CheersMate.cheersmate.domain.enums.Volume;
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

    private final RestTemplate restTemplate;
    private final WeatherDataRepository weatherDataRepository;

    private final String FLASK_SERVER_URL = "http://52.79.37.145:5001";

    // 생성자에서 RestTemplate UTF-8 설정
    public RecommendationService(RestTemplate restTemplate,
                                 WeatherDataRepository weatherDataRepository) {
        this.restTemplate = restTemplate;
        this.weatherDataRepository = weatherDataRepository;
        this.restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    /**
     * (A) 일반 추천
     */
    public FrontendRecommendationResponse getFrontendRecommendation(RecommendationRequest request) {
        RecommendationResponse flaskResponse = getRecommendation(request);
        return transformToFrontendResponse(flaskResponse);
    }

    /**
     * (B) Flask /recommend 호출
     */
    public RecommendationResponse getRecommendation(RecommendationRequest request) {
        // 최신 날씨
        WeatherData latestWeather = weatherDataRepository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
        if (latestWeather == null) {
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }

        // 날씨 문자열 -> int
        int condition = WeatherUtil.mapWeatherConditionToInt(latestWeather.getWeatherCondition());
        int emotionCode = Emotion.fromString(request.getEmotion()).getCode();
        int companionCode = Companion.fromString(request.getCompanion()).getCode();
        int volumeCode = Volume.fromString(request.getVolume()).getCode();

        // Flask Request
        RecommendationRequestWithCondition flaskRequest = new RecommendationRequestWithCondition();
        flaskRequest.setCondition(condition);
        flaskRequest.setEmotion(emotionCode);
        flaskRequest.setCompanion(companionCode);
        flaskRequest.setVolume(volumeCode);

        // POST /recommend
        String url = FLASK_SERVER_URL + "/recommend";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var httpEntity = new HttpEntity<>(flaskRequest, headers);
        var response = restTemplate.exchange(url, HttpMethod.POST,
                httpEntity, RecommendationResponse.class);

        if (response.getBody() == null || !response.getBody().isResult()) {
            String errorMessage = (response.getBody() != null)
                    ? response.getBody().getError()
                    : "Flask 서버로부터 응답을 받지 못했습니다.";
            throw new RuntimeException(errorMessage);
        }
        return response.getBody();
    }

    /**
     * (C) 날씨 추천
     */
    public FrontendRecommendationResponse getWeatherRecommendation() {
        WeatherRecommendationResponse flaskResponse = getWeatherBasedRecommendation();
        return transformToFrontendWeatherResponse(flaskResponse);
    }

    public WeatherRecommendationResponse getWeatherBasedRecommendation() {
        WeatherData latestWeather = weatherDataRepository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
        if (latestWeather == null) {
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }
        int condition = WeatherUtil.mapWeatherConditionToInt(latestWeather.getWeatherCondition());

        String url = FLASK_SERVER_URL + "/recommend/weather?condition=" + condition;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(headers), WeatherRecommendationResponse.class);

        if (response.getBody() == null || !response.getBody().isResult()) {
            String errorMessage = (response.getBody() != null)
                    ? response.getBody().getError()
                    : "Flask 서버로부터 응답을 받지 못했습니다.";
            throw new RuntimeException(errorMessage);
        }
        return response.getBody();
    }


    // Flask 응답 -> FrontendRecommendationResponse 변환
    private FrontendRecommendationResponse transformToFrontendResponse(RecommendationResponse flaskResponse) {
        FrontendRecommendationResponse frontendResponse = new FrontendRecommendationResponse();
        frontendResponse.setResult(flaskResponse.isResult());
        frontendResponse.setHttpCode(flaskResponse.getHttpCode());
        frontendResponse.setError(flaskResponse.getError());

        FrontendRecommendationResponse.Data frontendData = new FrontendRecommendationResponse.Data();
        // Request
        FrontendRecommendationResponse.Request frontendRequest = new FrontendRecommendationResponse.Request();
        frontendRequest.setWeather(flaskResponse.getData().getRequest().getWeather());
        frontendRequest.setEmotion(flaskResponse.getData().getRequest().getEmotion());
        frontendRequest.setCompanion(flaskResponse.getData().getRequest().getCompanion());
        frontendData.setRequest(frontendRequest);

        // Recommend
        RecommendationResponse.Recommend flaskRec = flaskResponse.getData().getRecommend();
        FrontendRecommendationResponse.Liquor recLiquor = new FrontendRecommendationResponse.Liquor();
        recLiquor.setName(flaskRec.getName());
        recLiquor.setVolume(flaskRec.getVolume());
        recLiquor.setType(flaskRec.getType());
        recLiquor.setImageUrl(flaskRec.getImageUrl());

        FrontendRecommendationResponse.Recommend recommend = new FrontendRecommendationResponse.Recommend();
        recommend.setLiquor(recLiquor);

        List<FrontendRecommendationResponse.Recommend> recommendList = new ArrayList<>();
        recommendList.add(recommend);
        frontendData.setRecommend(recommendList);

        // Food
        List<FrontendRecommendationResponse.Food> foodList = new ArrayList<>();
        for (RecommendationResponse.Food flaskFood : flaskResponse.getData().getFood()) {
            FrontendRecommendationResponse.Food f = new FrontendRecommendationResponse.Food();
            f.setName(flaskFood.getName());
            f.setImageUrl(flaskFood.getImageUrl());
            foodList.add(f);
        }
        frontendData.setFood(foodList);

        // Similar
        List<FrontendRecommendationResponse.SimilarItem> similarList = new ArrayList<>();
        for (RecommendationResponse.SimilarItem flaskSimilar : flaskResponse.getData().getSimilar()) {
            FrontendRecommendationResponse.SimilarItem si = new FrontendRecommendationResponse.SimilarItem();
            FrontendRecommendationResponse.Liquor sLiquor = new FrontendRecommendationResponse.Liquor();
            sLiquor.setName(flaskSimilar.getName());
            sLiquor.setVolume(flaskSimilar.getVolume());
            sLiquor.setType(flaskSimilar.getType());
            sLiquor.setImageUrl(flaskSimilar.getImageUrl());

            si.setLiquor(sLiquor);
            similarList.add(si);
        }
        frontendData.setSimilar(similarList);

        frontendResponse.setData(frontendData);
        return frontendResponse;
    }

    private FrontendRecommendationResponse transformToFrontendWeatherResponse(WeatherRecommendationResponse flaskResponse) {
        FrontendRecommendationResponse frontendResponse = new FrontendRecommendationResponse();
        frontendResponse.setResult(flaskResponse.isResult());
        frontendResponse.setHttpCode(flaskResponse.getHttpCode());
        frontendResponse.setError(flaskResponse.getError());

        FrontendRecommendationResponse.Data data = new FrontendRecommendationResponse.Data();
        FrontendRecommendationResponse.Request req = new FrontendRecommendationResponse.Request();
        req.setWeather(flaskResponse.getData().getRequest().getWeather());
        data.setRequest(req);

        // recommend 리스트
        List<FrontendRecommendationResponse.Recommend> recList = new ArrayList<>();
        for (WeatherRecommendationResponse.Recommend flaskRec : flaskResponse.getData().getRecommend()) {
            FrontendRecommendationResponse.Liquor liq = new FrontendRecommendationResponse.Liquor();
            liq.setName(flaskRec.getName());
            liq.setVolume(flaskRec.getVolume());
            liq.setType(flaskRec.getType());
            liq.setImageUrl(flaskRec.getImageUrl());

            FrontendRecommendationResponse.Recommend r = new FrontendRecommendationResponse.Recommend();
            r.setLiquor(liq);
            recList.add(r);
        }
        data.setRecommend(recList);

        // food, similar는 null
        data.setFood(null);
        data.setSimilar(null);

        frontendResponse.setData(data);
        return frontendResponse;
    }
}
