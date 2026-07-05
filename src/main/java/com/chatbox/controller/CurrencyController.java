package com.chatbox.controller;

import com.chatbox.model.CurrencyData;
import com.chatbox.client.CurrencyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class CurrencyController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    private final CurrencyClient currencyClient;

    public CurrencyController(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCurrencies() {
        try {
            logger.info("Fetching all currencies");

            CurrencyData currencyData = currencyClient.getAllCurrencies();

            if (currencyData == null || currencyData.getCurrencies() == null) {
                return ResponseEntity.badRequest().body("Unable to fetch currency data");
            }

            logger.info("Currency data retrieved successfully");
            return ResponseEntity.ok(currencyData);

        } catch (Exception e) {
            logger.error("Error fetching currencies", e);
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{currencyId}")
    public ResponseEntity<?> getCurrencyById(@PathVariable String currencyId) {
        try {
            logger.info("Fetching currency: {}", currencyId);

            CurrencyData.Currency currency = currencyClient.getCurrencyById(currencyId);

            if (currency == null) {
                return ResponseEntity.badRequest().body("Currency not found: " + currencyId);
            }

            logger.info("Currency data retrieved successfully for: {}", currencyId);
            return ResponseEntity.ok(currency);

        } catch (Exception e) {
            logger.error("Error fetching currency", e);
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all/formatted")
    public ResponseEntity<?> getFormattedCurrencies() {
        try {
            logger.info("Fetching formatted currencies");

            CurrencyData currencyData = currencyClient.getAllCurrencies();
            String formattedData = currencyClient.formatCurrencies(currencyData);

            logger.info("Formatted currency data prepared");
            return ResponseEntity.ok(formattedData);

        } catch (Exception e) {
            logger.error("Error fetching formatted currencies", e);
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{currencyId}/formatted")
    public ResponseEntity<?> getFormattedCurrency(@PathVariable String currencyId) {
        try {
            logger.info("Fetching formatted currency: {}", currencyId);

            CurrencyData.Currency currency = currencyClient.getCurrencyById(currencyId);
            String formattedData = currencyClient.formatCurrency(currency);

            logger.info("Formatted currency data prepared for: {}", currencyId);
            return ResponseEntity.ok(formattedData);

        } catch (Exception e) {
            logger.error("Error fetching formatted currency", e);
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }
}
