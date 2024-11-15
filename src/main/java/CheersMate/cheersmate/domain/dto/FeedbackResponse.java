package CheersMate.cheersmate.domain.dto;

public class FeedbackResponse {

    private boolean result;
    private int httpCode;
    private String text;

    // 생성자
    public FeedbackResponse(boolean result, int httpCode, String text) {
        this.result = result;
        this.httpCode = httpCode;
        this.text = text;
    }

    // Getters and Setters
    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
