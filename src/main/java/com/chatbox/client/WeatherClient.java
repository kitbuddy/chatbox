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
    
    @Value("${weather.api.url:http://api.weatherapi.com/v1}")
    private String weatherApiUrl;
    
    private final RestTemplate restTemplate;

    public WeatherClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherData getWeatherByCityName(String cityName) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(weatherApiUrl)
                    .path("/current.json")
                    .queryParam("key", apiKey)
                    .queryParam("q", cityName)
                    .queryParam("aqi", "no")
                    .build()
                    .toUriString();
            
            logger.info("Fetching weather for city: {}", cityName);
            logger.debug("URL: {}", url);
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
        String cityName = weather.getCityName();
        sb.append("City: ").append(cityName != null ? cityName : "Unknown").append("\n");
        
        if (weather.getMain() != null) {
            Double temp = weather.getMain().getTemp();
            Integer humidity = weather.getMain().getHumidity();
            Double pressure = weather.getMain().getPressure();
            
            if (temp != null) sb.append("Temperature: ").append(temp).append("°C\n");
            if (humidity != null) sb.append("Humidity: ").append(humidity).append("%\n");
            if (pressure != null) sb.append("Pressure: ").append(pressure).append(" mb\n");
        }
        
        if (weather.getWind() != null) {
            Double speed = weather.getWind().getSpeed();
            if (speed != null) sb.append("Wind Speed: ").append(String.format("%.2f", speed)).append(" m/s\n");
        }
        
        if (weather.getWeather() != null && !weather.getWeather().isEmpty()) {
            WeatherData.Weather w = weather.getWeather().get(0);
            sb.append("Condition: ").append(w.getDescription()).append("\n");
        }
        
        return sb.toString();
    }
}

