package com.chatbox.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CurrencyData {
    @JsonProperty("data")
    private List<Currency> currencies;

    public CurrencyData() {
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public static class Currency {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("min_size")
        private String minSize;

        public Currency() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMinSize() {
            return minSize;
        }

        public void setMinSize(String minSize) {
            this.minSize = minSize;
        }
    }
}
