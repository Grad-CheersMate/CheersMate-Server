package CheersMate.cheersmate.domain.enums;

public enum Volume {
    LIGHT(0),
    TIPSY(1),
    BUZZED(2),
    DRUNKED(3);

    private final int code;

    Volume(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Volume fromString(String text) {
        for (Volume v : Volume.values()) {
            if (v.name().equalsIgnoreCase(text)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Unknown volume: " + text);
    }
}
