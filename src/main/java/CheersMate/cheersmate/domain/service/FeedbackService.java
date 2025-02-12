package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.FeedbackRequest;
import CheersMate.cheersmate.domain.dto.FeedbackStatisticsDTO;
import CheersMate.cheersmate.domain.dto.RatingDTO;
import CheersMate.cheersmate.domain.entity.Feedback;
import CheersMate.cheersmate.domain.entity.Liquor;
import CheersMate.cheersmate.domain.enums.Companion;
import CheersMate.cheersmate.domain.enums.Emotion;
import CheersMate.cheersmate.domain.repository.FeedbackRepository;
import CheersMate.cheersmate.domain.repository.LiquorRepository;
import CheersMate.cheersmate.domain.util.WeatherUtil;
import CheersMate.cheersmate.weather.entity.WeatherData;
import CheersMate.cheersmate.weather.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final LiquorRepository liquorRepository;
    private final WeatherDataRepository weatherDataRepository;

    /**
     * 1) 피드백 저장
     */
    public void saveFeedback(FeedbackRequest feedbackRequest) {
        // 최신 날씨 데이터
        WeatherData latestWeather = weatherDataRepository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
        if (latestWeather == null) {
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }

        // 날씨, 감정, 동행 → int 코드
        int weatherCondition = WeatherUtil.mapWeatherConditionToInt(latestWeather.getWeatherCondition());
        int emotionCode = Emotion.fromString(feedbackRequest.getEmotion()).getCode();
        int companionCode = Companion.fromString(feedbackRequest.getCompanion()).getCode();

        // Liquor 검색
        Optional<Liquor> optionalLiquor = liquorRepository.findByName(feedbackRequest.getLiquor().getName());
        if (optionalLiquor.isEmpty()) {
            throw new RuntimeException("주류 '" + feedbackRequest.getLiquor().getName() + "'를 찾을 수 없습니다.");
        }

        // Feedback 엔티티 생성
        Feedback feedback = new Feedback(
                weatherCondition,
                emotionCode,
                companionCode,
                optionalLiquor.get(),
                feedbackRequest.getRating()
        );

        feedbackRepository.save(feedback);
    }

    /**
     * 2) 피드백 통계
     */
    public FeedbackStatisticsDTO getFeedbackStatistics() {
        var statistics = feedbackRepository.getFeedbackStatistics();
        var combos = statistics.stream()
                .map(row -> new FeedbackStatisticsDTO.FeedbackCombinationDTO(
                        WeatherUtil.convertEmotion((Integer) row[0]),       // int -> Emotion name
                        WeatherUtil.convertWeatherCondition((Integer) row[1]), // int -> weather string
                        (Long) row[2], // positive
                        (Long) row[3]  // negative
                ))
                .toList();

        return new FeedbackStatisticsDTO(combos);
    }

    /**
     * 3) 상위 평점 주류
     */
    public List<RatingDTO> getTopRatedLiquors() {
        var top30 = PageRequest.of(0, 30);
        var results = feedbackRepository.findTopRatedLiquors(top30);

        return results.stream()
                .map(row -> new RatingDTO(
                        (Long) row[0],   // liquorId
                        (String) row[1], // liquorName
                        (Double) row[2], // volume
                        (String) row[3], // imageUrl
                        (String) row[4]  // type(category)
                ))
                .toList();
    }
}
