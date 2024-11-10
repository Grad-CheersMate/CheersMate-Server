package CheersMate.cheersmate.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class RecommendationResponse {

    private boolean result;
    private int httpCode;
    private Data data;
    private String error; // 에러 메시지 처리

    // Getters and Setters
    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // 내부 클래스 정의
    public static class Data {
        @JsonProperty("현재 상태")
        private CurrentState currentState;

        @JsonProperty("추천된 주류")
        private RecommendedLiquor recommendedLiquor;

        @JsonProperty("유사 주류")
        private List<SimilarLiquor> similarLiquors;

        // Getters and Setters
        public CurrentState getCurrentState() {
            return currentState;
        }

        public void setCurrentState(CurrentState currentState) {
            this.currentState = currentState;
        }

        public RecommendedLiquor getRecommendedLiquor() {
            return recommendedLiquor;
        }

        public void setRecommendedLiquor(RecommendedLiquor recommendedLiquor) {
            this.recommendedLiquor = recommendedLiquor;
        }

        public List<SimilarLiquor> getSimilarLiquors() {
            return similarLiquors;
        }

        public void setSimilarLiquors(List<SimilarLiquor> similarLiquors) {
            this.similarLiquors = similarLiquors;
        }
    }

    public static class CurrentState {
        @JsonProperty("날씨")
        private String weather;

        @JsonProperty("기분")
        private String mood;

        @JsonProperty("동반자")
        private String companion;

        // Getters and Setters
        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getMood() {
            return mood;
        }

        public void setMood(String mood) {
            this.mood = mood;
        }

        public String getCompanion() {
            return companion;
        }

        public void setCompanion(String companion) {
            this.companion = companion;
        }
    }

    public static class RecommendedLiquor {
        @JsonProperty("이름")
        private String name;

        @JsonProperty("주종")
        private String category;

        @JsonProperty("도수")
        private double alcoholContent;

        @JsonProperty("이미지 링크")
        private String imageLink;

        @JsonProperty("음식")
        private String food;

        @JsonProperty("음식 이미지 링크")
        private String foodImageLink;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getAlcoholContent() {
            return alcoholContent;
        }

        public void setAlcoholContent(double alcoholContent) {
            this.alcoholContent = alcoholContent;
        }

        public String getImageLink() {
            return imageLink;
        }

        public void setImageLink(String imageLink) {
            this.imageLink = imageLink;
        }

        public String getFood() {
            return food;
        }

        public void setFood(String food) {
            this.food = food;
        }

        public String getFoodImageLink() {
            return foodImageLink;
        }

        public void setFoodImageLink(String foodImageLink) {
            this.foodImageLink = foodImageLink;
        }
    }

    public static class SimilarLiquor {
        @JsonProperty("이름")
        private String name;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
