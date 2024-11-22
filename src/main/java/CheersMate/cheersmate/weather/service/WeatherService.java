package CheersMate.cheersmate.weather.service;

import CheersMate.cheersmate.weather.entity.WeatherData;
import CheersMate.cheersmate.weather.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final WeatherDataRepository repository;

    @Value("${weather.api.serviceKey}")
    private String serviceKey;

    /**
     * 스케줄링 메서드. 크론 표현식은 application.yml에서 주입됩니다.
     * 매 10분마다 실행됩니다.
     */
    @Scheduled(cron = "${weather.scheduling.cron}")
    public void scheduledFetchAndSave() {
        logger.info("Scheduled task started: Fetching and saving weather data.");
        fetchAndSaveWeatherData();
    }

    public void fetchAndSaveWeatherData() {
        try {
            // API 호출 및 JSON 데이터 가져오기
            JSONObject jsonData = getWeatherData();

            // JSON 데이터 파싱 및 WeatherData 객체 생성
            WeatherData weatherData = parseWeatherData(jsonData);

            // 중복 체크 후 데이터베이스에 저장
            if (repository.findByWeatherDateAndWeatherTime(weatherData.getWeatherDate(), weatherData.getWeatherTime()).isEmpty()) {
                repository.save(weatherData);
            } else {
                System.out.println("이미 해당 시간의 날씨 데이터가 존재합니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리 로직 추가 가능
        }
    }

    private JSONObject getWeatherData() throws IOException {
        // 현재 날짜와 시간에 맞게 base_date와 base_time을 계산
        LocalDate today = LocalDate.now();
        String baseDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = calculateBaseTime();

        // hour가 23이면 baseDate를 전날로 변경
        if ("2300".equals(baseTime)) {
            LocalDate yesterday = today.minusDays(1);
            baseDate = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("63", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("124", "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        // JSON 파싱
        JSONObject jsonObject = new JSONObject(sb.toString());
        return jsonObject;
    }

    private WeatherData parseWeatherData(JSONObject jsonData) {
        WeatherData weatherData = new WeatherData();

        JSONObject response = jsonData.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONObject items = body.getJSONObject("items");
        JSONArray itemArray = items.getJSONArray("item");

        // 필요한 변수들을 미리 선언
        String precipitationTypeCode = "0";
        String humidity = "0";
        String hourlyPrecipitation = "0";
        String temperature = "0";
        String windSpeed = "0";

        // 날짜와 시간 설정
        if (itemArray.length() > 0) {
            JSONObject firstItem = itemArray.getJSONObject(0);
            String baseDate = firstItem.getString("baseDate");
            String baseTime = firstItem.getString("baseTime");
            weatherData.setWeatherDate(
                    LocalDate.parse(baseDate, DateTimeFormatter.ofPattern("yyyyMMdd")));
            weatherData.setWeatherTime(
                    LocalTime.parse(baseTime, DateTimeFormatter.ofPattern("HHmm")));
        }

        // 각 카테고리에 따라 값 매핑
        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject item = itemArray.getJSONObject(i);
            String category = item.getString("category");
            String value = item.getString("obsrValue");

            switch (category) {
                case "PTY": // 강수 형태
                    precipitationTypeCode = value;
                    weatherData.setPrecipitationType(getPrecipitationType(value));
                    break;
                case "REH": // 습도
                    humidity = value;
                    weatherData.setHumidity(value);
                    break;
                case "RN1": // 1시간 강수량
                    hourlyPrecipitation = value;
                    weatherData.setHourlyPrecipitation(value);
                    break;
                case "T1H": // 기온
                    temperature = value;
                    weatherData.setTemperature(value);
                    break;
                case "UUU": // 동서 바람 성분
                    weatherData.setUComponentWind(value);
                    break;
                case "VEC": // 풍향
                    weatherData.setWindDirection(value);
                    break;
                case "VVV": // 남북 바람 성분
                    weatherData.setVComponentWind(value);
                    break;
                case "WSD": // 풍속
                    windSpeed = value;
                    weatherData.setWindSpeed(value);
                    break;
                default:
                    // 필요 없는 카테고리인 경우 처리하지 않음
                    break;
            }
        }

        // **날씨 상태 결정 로직 추가**
        String weatherCondition = determineWeatherCondition(
                precipitationTypeCode, humidity, hourlyPrecipitation, temperature, windSpeed);
        weatherData.setWeatherCondition(weatherCondition);

        return weatherData;
    }

    // **날씨 상태 결정 메서드 추가**
    private String determineWeatherCondition(
            String ptyCode, String rehValue, String rn1Value, String t1hValue, String wsdValue) {
        try {
            // 문자열 값을 숫자로 변환
            int pty = Integer.parseInt(ptyCode);
            int reh = Integer.parseInt(rehValue);
            double rn1 = Double.parseDouble(rn1Value);
            double t1h = Double.parseDouble(t1hValue);
            double wsd = Double.parseDouble(wsdValue);

            // 날씨 상태 변수
            String condition = "맑음";

            // 1. 비 또는 눈
            if (pty == 1 || pty == 2 || pty == 4) {
                condition = "rainy";
            } else if (pty == 3) {
                condition = "snowy";
            }
            // 2. 흐림
            else if (reh >= 80 && pty == 0) {
                condition = "cloudy";
            }
            // 3. 더운 날
            else if (t1h >= 30) {
                condition = "hot";
            }
            // 4. 추운 날
            else if (t1h <= 5) {
                condition = "cold";
            }
            // 5. 바람 부는 날
            else if (wsd >= 8) {
                condition = "windy";
            }
            // 6. 맑음
            else {
                condition = "sunny";
            }

            return condition;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "데이터 오류";
        }
    }

    // base_time 계산 로직
    private String calculateBaseTime() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        if (minute < 40) {
            hour = hour - 1;
            if (hour < 0) {
                hour = 23;
            }
        }

        LocalTime baseTime = LocalTime.of(hour, 0);
        return baseTime.format(DateTimeFormatter.ofPattern("HHmm"));
    }

    // 강수 형태 코드 변환
    private String getPrecipitationType(String code) {
        switch (code) {
            case "0": return "없음";
            case "1": return "비";
            case "2": return "비/눈";
            case "3": return "눈";
            case "4": return "소나기";
            default: return "알 수 없음";
        }
    }

    //가장 최근 날씨 데이터 조회
    public WeatherData getLatestWeatherData() {
        return repository.findTopByOrderByWeatherDateDescWeatherTimeDesc();
    }
}

