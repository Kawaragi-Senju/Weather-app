package ru.test.weathertestapp;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("weather")
public class MainView extends VerticalLayout {

    private final WeatherService weatherService;
    private Text weatherText;

    public MainView(WeatherService weatherService) {
        this.weatherService = weatherService;

        TextField latitudeField = new TextField("Широта");
        TextField longitudeField = new TextField("Долгота");
        Button showWeatherButton = new Button("Показать погоду");

        weatherText = new Text("");
        add(latitudeField, longitudeField, showWeatherButton, weatherText);

        showWeatherButton.addClickListener(e -> {
            String latitude = latitudeField.getValue();
            String longitude = longitudeField.getValue();
            String weatherData = weatherService.getWeatherData(latitude, longitude);
            weatherText.setText(weatherData);
        });

        Button showHistoryButton = new Button("Показать историю запросов");
        showHistoryButton.addClickListener(e -> {
            List<WeatherRequest> history = weatherService.getAllRequests();
            history.forEach(request -> {
                add(new Text("Запрос: " + request.getLatitude() + ", " + request.getLongitude() + " - " + request.getWeatherData() + " в " + request.getTimestamp()));
            });
        });
    }
}