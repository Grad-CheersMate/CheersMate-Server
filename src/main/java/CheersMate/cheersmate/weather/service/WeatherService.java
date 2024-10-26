package CheersMate.cheersmate.weather.service;


import CheersMate.cheersmate.weather.entity.WeatherData;
import CheersMate.cheersmate.weather.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
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

    private final WeatherDataRepository repository;

    @Value("${weather.api.serviceKey}")
    private String serviceKey;

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
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("55", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("127", "UTF-8"));

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

        // 날짜와 시간 설정
        if (itemArray.length() > 0) {
            JSONObject firstItem = itemArray.getJSONObject(0);
            String baseDate = firstItem.getString("baseDate");
            String baseTime = firstItem.getString("baseTime");
            weatherData.setWeatherDate(LocalDate.parse(baseDate, DateTimeFormatter.ofPattern("yyyyMMdd")));
            weatherData.setWeatherTime(LocalTime.parse(baseTime, DateTimeFormatter.ofPattern("HHmm")));
        }

        // 각 카테고리에 따라 값 매핑
        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject item = itemArray.getJSONObject(i);
            String category = item.getString("category");
            String value = item.getString("obsrValue");

            switch (category) {
                case "PTY": // 강수 형태
                    weatherData.setPrecipitationType(getPrecipitationType(value));
                    break;
                case "REH": // 습도
                    weatherData.setHumidity(value);
                    break;
                case "RN1": // 1시간 강수량
                    weatherData.setHourlyPrecipitation(value);
                    break;
                case "T1H": // 기온
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
                    weatherData.setWindSpeed(value);
                    break;
                default:
                    // 필요 없는 카테고리인 경우 처리하지 않음
                    break;
            }
        }

        return weatherData;
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
}

