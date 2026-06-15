package com.chatbox.model;

import java.time.LocalDateTime;

public class ChatResponse {
    private String response;
    private String apiData;
    private LocalDateTime timestamp;
    private String status;

    public ChatResponse() {
    }

    public ChatResponse(String response, String apiData, LocalDateTime timestamp, String status) {
        this.response = response;
        this.apiData = apiData;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getApiData() {
        return apiData;
    }

    public void setApiData(String apiData) {
        this.apiData = apiData;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

