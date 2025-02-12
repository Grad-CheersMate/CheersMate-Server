package CheersMate.cheersmate.domain.flask;

import CheersMate.cheersmate.domain.dto.RecommendationRequestWithCondition;
import CheersMate.cheersmate.domain.dto.RecommendationResponse;
import CheersMate.cheersmate.domain.dto.WeatherRecommendationResponse;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Component
public class FlaskRecommendationClient {

    private final RestTemplate restTemplate;
    private final String FLASK_SERVER_URL = "http://52.79.37.145:5001";

    public FlaskRecommendationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // UTF-8 인코딩 설정
        this.restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public RecommendationResponse getRecommendation(RecommendationRequestWithCondition request) {
        String url = FLASK_SERVER_URL + "/recommend";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecommendationRequestWithCondition> entity = new HttpEntity<>(request, headers);

        ResponseEntity<RecommendationResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, RecommendationResponse.class
        );

        if (response.getBody() == null || !response.getBody().isResult()) {
            String error = (response.getBody() != null) ? response.getBody().getError()
                    : "Flask 서버로부터 응답을 받지 못했습니다.";
            throw new RuntimeException(error);
        }
        return response.getBody();
    }

    public WeatherRecommendationResponse getWeatherRecommendation(int condition) {
        String url = FLASK_SERVER_URL + "/recommend/weather?condition=" + condition;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<WeatherRecommendationResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, WeatherRecommendationResponse.class
        );

        if (response.getBody() == null || !response.getBody().isResult()) {
            String error = (response.getBody() != null) ? response.getBody().getError()
                    : "Flask 서버로부터 응답을 받지 못했습니다.";
            throw new RuntimeException(error);
        }
        return response.getBody();
    }
}
