package CheersMate.cheersmate.domain.dto;

import java.util.List;

public class FrontendRecommendationResponse {

    private boolean result;
    private int httpCode;
    private Data data;
    private String error;

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
        private Request request;
        private Recommend recommend;
        private Food food;
        private List<SimilarItem> similar;

        // Getters and Setters

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public Recommend getRecommend() {
            return recommend;
        }

        public void setRecommend(Recommend recommend) {
            this.recommend = recommend;
        }

        public Food getFood() {
            return food;
        }

        public void setFood(Food food) {
            this.food = food;
        }

        public List<SimilarItem> getSimilar() {
            return similar;
        }

        public void setSimilar(List<SimilarItem> similar) {
            this.similar = similar;
        }
    }

    public static class Request {
        private String weather;
        private String emotion;
        private String companion;

        // Getters and Setters

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getEmotion() {
            return emotion;
        }

        public void setEmotion(String emotion) {
            this.emotion = emotion;
        }

        public String getCompanion() {
            return companion;
        }

        public void setCompanion(String companion) {
            this.companion = companion;
        }
    }

    public static class Recommend {
        private Liquor liquor;

        // Getters and Setters

        public Liquor getLiquor() {
            return liquor;
        }

        public void setLiquor(Liquor liquor) {
            this.liquor = liquor;
        }
    }

    public static class Liquor {
        private String name;
        private double volume;
        private String type;
        private String imageUrl;

        // Getters and Setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getVolume() {
            return volume;
        }

        public void setVolume(double volume) {
            this.volume = volume;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public static class Food {
        private String name;
        private String imageUrl;

        // Getters and Setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public static class SimilarItem {
        private Liquor liquor;

        // Getters and Setters

        public Liquor getLiquor() {
            return liquor;
        }

        public void setLiquor(Liquor liquor) {
            this.liquor = liquor;
        }
    }
}
