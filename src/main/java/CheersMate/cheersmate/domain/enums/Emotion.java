package CheersMate.cheersmate.domain.enums;

public enum Emotion {
    SMILE(0),
    SAD(1),
    CALM(2),
    ANGRY(3);

    private final int code;

    Emotion(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Emotion fromString(String value) {
        switch (value.toLowerCase()) {
            case "smile":
                return SMILE;
            case "sad":
                return SAD;
            case "calm":
                return CALM;
            case "angry":
                return ANGRY;
            default:
                throw new IllegalArgumentException("Unknown emotion: " + value);
        }
    }
}
