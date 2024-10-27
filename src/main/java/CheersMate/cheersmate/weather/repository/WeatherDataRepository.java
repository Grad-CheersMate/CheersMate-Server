package CheersMate.cheersmate.weather.repository;

import CheersMate.cheersmate.weather.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findByWeatherDateAndWeatherTime(LocalDate date, LocalTime time);

    // 가장 최근 데이터 조회 메서드 추가
    WeatherData findTopByOrderByWeatherDateDescWeatherTimeDesc();

}
