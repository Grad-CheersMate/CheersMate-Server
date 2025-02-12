package CheersMate.cheersmate.domain.util;


import CheersMate.cheersmate.domain.enums.Emotion;

public class WeatherUtil {

    /**
     * 날씨 문자열 -> int 코드 변환
     * RecommendationService / FeedbackService 등에서 공통으로 사용
     */
    public static int mapWeatherConditionToInt(String weatherCondition) {
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
                return 0; // 기본값
        }
    }

    /**
     * int -> 날씨 문자열
     */
    public static String convertWeatherCondition(int code) {
        return switch (code) {
            case 0 -> "sunny";
            case 1 -> "rainy";
            case 2 -> "snowy";
            case 3 -> "cloudy";
            case 4 -> "hot";
            case 5 -> "windy";
            case 6 -> "cold";
            default -> "undefined";
        };
    }

    /**
     * 감정 코드 -> 문자열 (Feedback 통계에서 필요)
     */
    public static String convertEmotion(int code) {
        for (Emotion emotion : Emotion.values()) {
            if (emotion.getCode() == code) {
                return emotion.name();
            }
        }
        return "UNKNOWN";
    }
}
