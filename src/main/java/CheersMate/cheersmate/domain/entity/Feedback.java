package CheersMate.cheersmate.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int weatherCondition;
    private int mood;
    private int companion;

    private String recommendedLiquor;
    private String drinkType;
    private double alcoholContent;
    private int rating;

    // 기본 생성자
    public Feedback() {}

    // 모든 필드를 포함하는 생성자
    public Feedback(int weatherCondition, int mood, int companion, String recommendedLiquor, String drinkType, double alcoholContent, int rating) {
        this.weatherCondition = weatherCondition;
        this.mood = mood;
        this.companion = companion;
        this.recommendedLiquor = recommendedLiquor;
        this.drinkType = drinkType;
        this.alcoholContent = alcoholContent;
        this.rating = rating;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    // 기타 필드에 대한 Getter와 Setter
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
