package com.chatbox.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherData {
    @JsonProperty("location")
    private Location location;
    
    @JsonProperty("current")
    private Current current;

    public WeatherData() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public String getCityName() {
        return location != null ? location.getName() : null;
    }

    public Main getMain() {
        if (current == null) return null;
        Main main = new Main();
        main.setTemp(current.getTemp_c());
        main.setHumidity(current.getHumidity());
        main.setPressure(current.getPressure_mb());
        return main;
    }

    public Wind getWind() {
        if (current == null) return null;
        Wind wind = new Wind();
        wind.setSpeed(current.getWind_kph() / 3.6);
        return wind;
    }

    public java.util.List<Weather> getWeather() {
        if (current == null || current.getCondition() == null) return null;
        Weather w = new Weather();
        w.setMain(current.getCondition().getText());
        w.setDescription(current.getCondition().getText());
        return java.util.Arrays.asList(w);
    }

    public static class Location {
        private String name;
        private String region;
        private String country;

        public Location() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public static class Current {
        @JsonProperty("temp_c")
        private Double temp_c;
        
        @JsonProperty("humidity")
        private Integer humidity;
        
        @JsonProperty("pressure_mb")
        private Double pressure_mb;
        
        @JsonProperty("wind_kph")
        private Double wind_kph;
        
        @JsonProperty("condition")
        private Condition condition;

        public Current() {
        }

        public Double getTemp_c() {
            return temp_c;
        }

        public void setTemp_c(Double temp_c) {
            this.temp_c = temp_c;
        }

        public Integer getHumidity() {
            return humidity;
        }

        public void setHumidity(Integer humidity) {
            this.humidity = humidity;
        }

        public Double getPressure_mb() {
            return pressure_mb;
        }

        public void setPressure_mb(Double pressure_mb) {
            this.pressure_mb = pressure_mb;
        }

        public Double getWind_kph() {
            return wind_kph;
        }

        public void setWind_kph(Double wind_kph) {
            this.wind_kph = wind_kph;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }
    }

    public static class Condition {
        private String text;

        public Condition() {
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Main {
        private Double temp;
        private Integer humidity;
        private Double pressure;

        public Main() {
        }

        public Double getTemp() {
            return temp;
        }

        public void setTemp(Double temp) {
            this.temp = temp;
        }

        public Integer getHumidity() {
            return humidity;
        }

        public void setHumidity(Integer humidity) {
            this.humidity = humidity;
        }

        public Double getPressure() {
            return pressure;
        }

        public void setPressure(Double pressure) {
            this.pressure = pressure;
        }
    }

    public static class Weather {
        private String main;
        private String description;

        public Weather() {
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Wind {
        private Double speed;

        public Wind() {
        }

        public Double getSpeed() {
            return speed;
        }

        public void setSpeed(Double speed) {
            this.speed = speed;
        }
    }
}

