package CheersMate.cheersmate.domain.entity;

import jakarta.persistence.*;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int weatherCondition;
    private int emotion; // 기존 mood를 emotion으로 변경
    private int companion;
    private int rating;

    @ManyToOne
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    // 기본 생성자
    public Feedback() {}

    // Liquor 객체를 포함하는 생성자
    public Feedback(int weatherCondition, int emotion, int companion, Liquor liquor, int rating) {
        this.weatherCondition = weatherCondition;
        this.emotion = emotion;
        this.companion = companion;
        this.liquor = liquor;
        this.rating = rating;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public int getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(int weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Liquor getLiquor() {
        return liquor;
    }

    public void setLiquor(Liquor liquor) {
        this.liquor = liquor;
    }
}
