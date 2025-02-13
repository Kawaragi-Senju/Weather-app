package ru.test.weathertestapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherService {

    @Value("${openweather.api.key}")
    private String apiKey;

    private final WeatherRequestRepository weatherRequestRepository;

    public WeatherService(WeatherRequestRepository weatherRequestRepository) {
        this.weatherRequestRepository = weatherRequestRepository;
    }

    public String getWeatherData(String latitude, String longitude) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric&lang=ru",
                latitude, longitude, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String weatherData = parseWeatherResponse(response.body());

            WeatherRequest weatherRequest = new WeatherRequest();
            weatherRequest.setLatitude(latitude);
            weatherRequest.setLongitude(longitude);
            weatherRequest.setWeatherData(weatherData);
            weatherRequest.setTimestamp(LocalDateTime.now());

            weatherRequestRepository.save(weatherRequest);

            return weatherData;
        } catch (Exception e) {
            return "Ошибка получения данных о погоде.";
        }
    }

    public String parseWeatherResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            double temperature = rootNode.path("main").path("temp").asDouble();
            String description = rootNode.path("weather").get(0).path("description").asText();
            return String.format("Температура: %.1f°C, Погода: %s", temperature, description);
        } catch (Exception e) {
            return "Ошибка получения данных о погоде.";
        }
    }

    public List<WeatherRequest> getAllRequests() {
        return weatherRequestRepository.findAll();
    }
}