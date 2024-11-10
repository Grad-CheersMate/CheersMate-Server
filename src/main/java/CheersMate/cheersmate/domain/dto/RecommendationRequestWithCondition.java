package CheersMate.cheersmate.domain.dto;

public class RecommendationRequestWithCondition {

    private int condition;
    private int mood;
    private int companion;

    // Getters and Setters
    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
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
}
