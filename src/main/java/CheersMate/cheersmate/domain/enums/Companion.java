package CheersMate.cheersmate.domain.enums;

public enum Companion {
    SOLO(0),
    COUPLE(1),
    FRIEND(2),
    PEOPLE(3),
    FAMILY(4);

    private final int code;

    Companion(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Companion fromString(String value) {
        switch (value.toLowerCase()) {
            case "solo":
                return SOLO;
            case "couple":
                return COUPLE;
            case "friend":
                return FRIEND;
            case "people":
                return PEOPLE;
            case "family":
                return FAMILY;
            default:
                throw new IllegalArgumentException("Unknown companion: " + value);
        }
    }
}
