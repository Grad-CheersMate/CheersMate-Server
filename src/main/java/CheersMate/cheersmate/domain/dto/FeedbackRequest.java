package CheersMate.cheersmate.domain.dto;

public class FeedbackRequest {

    private int weatherCondition;
    private int mood;
    private int companion;

    private String recommendedLiquor;
    private String drinkType;
    private double alcoholContent;
    private int rating;

    // Getters and Setters
    public int getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(int weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public int getCompanion() {
        return companion;
    }

    public void setCompanion(int companion) {
        this.companion = companion;
    }

    public String getRecommendedLiquor() {
        return recommendedLiquor;
    }

    public void setRecommendedLiquor(String recommendedLiquor) {
        this.recommendedLiquor = recommendedLiquor;
    }

    public String getDrinkType() {
        return drinkType;
    }

    public void setDrinkType(String drinkType) {
        this.drinkType = drinkType;
    }

    public double getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
