package CheersMate.cheersmate.weather.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long weatherId; // 날씨 ID

    private LocalDate weatherDate; // 날짜
    private LocalTime weatherTime; // 시간

    private String precipitationType; // 강수 형태
    private String humidity; // 습도
    private String hourlyPrecipitation; // 1시간 강수량
    private String uComponentWind; // 동서 바람 성분
    private String windDirection; // 풍향
    private String vComponentWind; // 남북 바람 성분
    private String windSpeed; // 풍속
    private String temperature; // 기온
}
