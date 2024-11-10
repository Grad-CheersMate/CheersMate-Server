package CheersMate.cheersmate.weather.controller;


import CheersMate.cheersmate.response.ApiResponse;
import CheersMate.cheersmate.response.WeathersResponse;
import CheersMate.cheersmate.weather.entity.WeatherData;
import CheersMate.cheersmate.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather/fetch")
    public String fetchWeatherData() {
        weatherService.fetchAndSaveWeatherData();
        return "Weather data fetched and saved.";
    }

    @GetMapping("/weather")
    public ResponseEntity<?> getLatestWeatherData() {
        WeatherData weatherData = weatherService.getLatestWeatherData();

        if (weatherData != null) {
            log.info("{\"result\": 1, \"httpCode\": 200, \"weather\": {}}", weatherData);
            return ResponseEntity.ok(new WeathersResponse(true, 200, weatherData));
        } else {
            log.info("{\"result\": 0, \"httpCode\": 600}");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, 600));
        }
    }
}