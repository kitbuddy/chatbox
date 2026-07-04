package com.chatbox.service;

import com.chatbox.model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

@Service
public class ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final RestTemplate restTemplate;
    private final OllamaService ollamaService;

    public ChatService(RestTemplate restTemplate, OllamaService ollamaService) {
        this.restTemplate = restTemplate;
        this.ollamaService = ollamaService;
    }

    public ChatResponse processMessage(String userMessage) {
        try {
            logger.info("Processing user message: {}", userMessage);
            
            String apiData = "";
            
            // Check if message contains weather-related keywords
            if (containsWeatherKeywords(userMessage)) {
                String city = extractCityName(userMessage);
                if (city != null && !city.isEmpty()) {
                    logger.info("Weather query detected for city: {}", city);
                    apiData = fetchWeatherFromController(city);
                    logger.info("Weather data retrieved: {}", apiData);
                }
            }
            
            // Build prompt for Ollama
            String prompt = buildPrompt(userMessage, apiData);
            
            // Process with Ollama
            String ollamaResponse = ollamaService.processWithOllama(prompt);
            
            ChatResponse response = new ChatResponse();
            response.setResponse(ollamaResponse);
            response.setApiData(apiData);
            response.setTimestamp(LocalDateTime.now());
            response.setStatus("success");
            
            logger.info("Chat response prepared successfully");
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing message", e);
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setResponse("Error processing your request: " + e.getMessage());
            errorResponse.setStatus("error");
            errorResponse.setTimestamp(LocalDateTime.now());
            return errorResponse;
        }
    }

    private boolean containsWeatherKeywords(String message) {
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("weather") || 
               lowerMessage.contains("temperature") || 
               lowerMessage.contains("forecast") ||
               lowerMessage.contains("rain") ||
               lowerMessage.contains("wind") ||
               lowerMessage.contains("humidity") ||
               lowerMessage.contains("hot") ||
               lowerMessage.contains("cold");
    }

    private String extractCityName(String message) {
        // Simple extraction - looks for "in <city>" pattern
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains(" in ")) {
            int inIndex = lowerMessage.indexOf(" in ");
            String afterIn = message.substring(inIndex + 4).trim();
            
            // Get first word or until punctuation
            String[] words = afterIn.split("[\\s.,?!]");
            if (words.length > 0 && !words[0].isEmpty()) {
                return words[0];
            }
        }
        
        return null;
    }

    private String buildPrompt(String userMessage, String apiData) {
        if (apiData == null || apiData.isEmpty()) {
            return userMessage + "\n\nPlease respond in English.";
        }
        
        return String.format(
            "User asked: %s\n\n" +
            "Here is relevant data from an external API:\n%s\n\n" +
            "Based on the above data, please provide a helpful response in clear English.",
            userMessage, apiData
        );
    }

    private String fetchWeatherFromController(String city) {
        try {
            String url = String.format("http://localhost:8080/api/weather/formatted?city=%s", 
                    java.net.URLEncoder.encode(city, "UTF-8"));
            logger.debug("Calling weather controller: {}", url);
            String weatherData = restTemplate.getForObject(url, String.class);
            return weatherData != null ? weatherData : "";
        } catch (Exception e) {
            logger.error("Error calling weather controller for city: {}", city, e);
            return "";
        }
    }
}
