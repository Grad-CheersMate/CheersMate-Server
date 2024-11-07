package CheersMate.cheersmate.domain.dto;

public class RecommendationResponse {

    private String recommendedDrink;
    private String drinkType;
    private double alcoholContent;
    private String imageUrl;
    private String currentStatus;

    // Getters and Setters
    public String getRecommendedDrink() {
        return recommendedDrink;
    }

    public void setRecommendedDrink(String recommendedDrink) {
        this.recommendedDrink = recommendedDrink;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
