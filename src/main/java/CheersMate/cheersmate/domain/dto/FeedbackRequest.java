package CheersMate.cheersmate.domain.dto;

import CheersMate.cheersmate.domain.enums.Companion;
import CheersMate.cheersmate.domain.enums.Emotion;

public class FeedbackRequest {

    private String emotion;
    private String companion;
    private LiquorName liquor; // 주류 이름을 객체로 받음
    private int rating;

    // Getters and Setters
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

    public LiquorName getLiquor() {
        return liquor;
    }

    public void setLiquor(LiquorName liquor) {
        this.liquor = liquor;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public static class LiquorName {
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
