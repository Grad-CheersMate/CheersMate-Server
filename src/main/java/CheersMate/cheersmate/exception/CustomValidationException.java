package CheersMate.cheersmate.exception;

import lombok.Getter;

@Getter
public class CustomValidationException extends RuntimeException {
    private final int errorCode;

    public CustomValidationException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
