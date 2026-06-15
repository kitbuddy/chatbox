package com.chatbox.service;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {
    
    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);
    
    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;
    
    @Value("${ollama.model:llama2}")
    private String modelName;
    
    private ChatLanguageModel chatModel;

    public OllamaService() {
    }

    private ChatLanguageModel getChatModel() {
        if (chatModel == null) {
            chatModel = OllamaChatModel.builder()
                    .baseUrl(ollamaBaseUrl)
                    .modelName(modelName)
                    .timeout(java.time.Duration.ofMinutes(5))
                    .build();
        }
        return chatModel;
    }

    public String processWithOllama(String prompt) {
        try {
            logger.info("Processing prompt with Ollama: {}", modelName);
            String response = getChatModel().generate(prompt);
            logger.info("Ollama response generated successfully");
            return response;
        } catch (Exception e) {
            logger.error("Error processing with Ollama", e);
            return "Error processing request: " + e.getMessage();
        }
    }
}

