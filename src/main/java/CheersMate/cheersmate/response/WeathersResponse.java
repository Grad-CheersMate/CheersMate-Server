package CheersMate.cheersmate.response;

import CheersMate.cheersmate.weather.entity.WeatherData;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WeathersResponse extends ApiResponse {
    private WeatherData weather;

    public WeathersResponse(boolean result, int httpCode, WeatherData weather) {
        super(result, httpCode);
        this.weather = weather;
    }
}
