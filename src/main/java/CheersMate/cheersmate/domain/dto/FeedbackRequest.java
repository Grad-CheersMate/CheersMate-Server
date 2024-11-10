package CheersMate.cheersmate.domain.dto;

public class FeedbackRequest {

    private int emotion;
    private int companion;
    private LiquorName liquor; // 주류 이름을 객체로 받음
    private int rating;

    // Getters and Setters
    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public int getCompanion() {
        return companion;
    }

    public void setCompanion(int companion) {
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
