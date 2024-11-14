package CheersMate.cheersmate.domain.dto;

import java.util.List;

public class WeatherRecommendationResponse {

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
        private List<Recommend> recommend; // 리스트


        // Getters and Setters

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public List<Recommend> getRecommend() {
            return recommend;
        }

        public void setRecommend(List<Recommend> recommend) {
            this.recommend = recommend;
        }
    }

    public static class Request {
        private String weather;

        // Getters and Setters

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }
    }

    public static class Recommend {
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
}
