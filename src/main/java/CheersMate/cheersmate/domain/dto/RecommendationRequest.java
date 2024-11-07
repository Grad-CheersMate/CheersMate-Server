package CheersMate.cheersmate.domain.dto;

public class RecommendationRequest {

    private int mood;
    private int companion;


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
