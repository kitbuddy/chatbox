package com.chatbox.client;

import com.chatbox.model.CurrencyData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyClient {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyClient.class);

    @Value("${currency.api.url:https://api.coinbase.com/v2/currencies}")
    private String currencyApiUrl;

    private final RestTemplate restTemplate;

    public CurrencyClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CurrencyData getAllCurrencies() {
        try {
            logger.info("Fetching all currencies from Coinbase API");
            logger.debug("URL: {}", currencyApiUrl);
            CurrencyData currencies = restTemplate.getForObject(currencyApiUrl, CurrencyData.class);
            logger.info("Currencies fetched successfully");
            return currencies;
        } catch (Exception e) {
            logger.error("Error fetching currencies", e);
            return null;
        }
    }

    public CurrencyData.Currency getCurrencyById(String currencyId) {
        try {
            CurrencyData allCurrencies = getAllCurrencies();
            if (allCurrencies != null && allCurrencies.getCurrencies() != null) {
                return allCurrencies.getCurrencies().stream()
                        .filter(c -> c.getId().equalsIgnoreCase(currencyId))
                        .findFirst()
                        .orElse(null);
            }
            return null;
        } catch (Exception e) {
            logger.error("Error fetching currency: {}", currencyId, e);
            return null;
        }
    }

    public String formatCurrencies(CurrencyData currencyData) {
        if (currencyData == null || currencyData.getCurrencies() == null) {
            return "Could not fetch currency data";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Available Currencies (Total: ").append(currencyData.getCurrencies().size()).append(")\n");
        sb.append("=============================================\n");

        currencyData.getCurrencies().forEach(currency ->
            sb.append(String.format("%-5s | %-40s | Min Size: %s\n",
                    currency.getId(),
                    currency.getName(),
                    currency.getMinSize()))
        );

        return sb.toString();
    }

    public String formatCurrency(CurrencyData.Currency currency) {
        if (currency == null) {
            return "Currency not found";
        }

        return String.format("Currency: %s\nName: %s\nMinimum Size: %s",
                currency.getId(),
                currency.getName(),
                currency.getMinSize());
    }
}
