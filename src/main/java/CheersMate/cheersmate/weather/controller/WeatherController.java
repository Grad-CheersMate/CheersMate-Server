package CheersMate.cheersmate.weather.controller;


import CheersMate.cheersmate.weather.entity.WeatherData;
import CheersMate.cheersmate.weather.repository.WeatherDataRepository;
import CheersMate.cheersmate.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherDataRepository repository;

    @GetMapping("/weather/fetch")
    public String fetchWeatherData() {
        weatherService.fetchAndSaveWeatherData();
        return "Weather data fetched and saved.";
    }

    @GetMapping("/weather")
    public WeatherData getLatestWeatherData() {
        return repository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
    }
}