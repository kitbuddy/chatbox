package com.chatbox.controller;

import com.chatbox.model.WeatherData;
import com.chatbox.client.WeatherClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class WeatherController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    
    private final WeatherClient weatherClient;

    public WeatherController(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> getWeather(
            @RequestParam String city,
            @RequestParam(required = false) String state) {
        
        try {
            logger.info("Fetching weather for city: {}, state: {}", city, state);
            
            String query = city;
            if (state != null && !state.trim().isEmpty()) {
                query = city + "," + state;
            }
            
            WeatherData weatherData = weatherClient.getWeatherByCityName(query);
            
            if (weatherData == null) {
                return ResponseEntity.badRequest().body("Unable to fetch weather data for the specified location");
            }
            
            logger.info("Weather data retrieved successfully for: {}", query);
            return ResponseEntity.ok(weatherData);
            
        } catch (Exception e) {
            logger.error("Error fetching weather", e);
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/formatted")
    public ResponseEntity<?> getFormattedWeather(
            @RequestParam String city,
            @RequestParam(required = false) String state) {
        
        try {
            logger.info("Fetching formatted weather for city: {}, state: {}", city, state);
            
            String query = city;
            if (state != null && !state.trim().isEmpty()) {
                query = city + "," + state;
            }
            
            WeatherData weatherData = weatherClient.getWeatherByCityName(query);
            String formattedData = weatherClient.formatWeatherData(weatherData);
            
            logger.info("Formatted weather data prepared for: {}", query);
            return ResponseEntity.ok(formattedData);
            
        } catch (Exception e) {
            logger.error("Error fetching formatted weather", e);
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }
}
