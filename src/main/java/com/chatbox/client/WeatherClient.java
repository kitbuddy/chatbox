package com.chatbox.client;

import com.chatbox.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherClient {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherClient.class);
    
    @Value("${weather.api.key:demo}")
    private String apiKey;
    
    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String weatherApiUrl;
    
    private final RestTemplate restTemplate;

    public WeatherClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherData getWeatherByCityName(String cityName) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(weatherApiUrl)
                    .queryParam("q", cityName)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build()
                    .toUriString();
            
            logger.info("Fetching weather for city: {}", cityName);
            WeatherData weather = restTemplate.getForObject(url, WeatherData.class);
            logger.info("Weather fetched successfully for: {}", cityName);
            return weather;
        } catch (Exception e) {
            logger.error("Error fetching weather for city: {}", cityName, e);
            return null;
        }
    }

    public String formatWeatherData(WeatherData weather) {
        if (weather == null) {
            return "Could not fetch weather data";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("City: ").append(weather.getCityName()).append("\n");
        
        if (weather.getMain() != null) {
            sb.append("Temperature: ").append(weather.getMain().getTemp()).append("°C\n");
            sb.append("Humidity: ").append(weather.getMain().getHumidity()).append("%\n");
            sb.append("Pressure: ").append(weather.getMain().getPressure()).append(" hPa\n");
        }
        
        if (weather.getWind() != null) {
            sb.append("Wind Speed: ").append(weather.getWind().getSpeed()).append(" m/s\n");
        }
        
        if (weather.getWeather() != null && !weather.getWeather().isEmpty()) {
            WeatherData.Weather w = weather.getWeather().get(0);
            sb.append("Condition: ").append(w.getDescription()).append("\n");
        }
        
        return sb.toString();
    }
}

