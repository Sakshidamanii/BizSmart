package com.bizsmart.controllers;

import com.bizsmart.models.DemandForecast;
import com.bizsmart.payload.response.DashboardSummaryResponse;
import com.bizsmart.payload.response.MessageResponse;
import com.bizsmart.payload.response.PredictionClientResponse;
import com.bizsmart.services.AnalyticsService;
import com.bizsmart.services.DemandPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private DemandPredictionService demandPredictionService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(analyticsService.getDashboardSummary());
    }

    @PostMapping("/forecast/{productId}")
    public ResponseEntity<?> generateForecast(
            @PathVariable Long productId,
            @RequestParam(required = false, defaultValue = "0.0") Double discountPercent,
            @RequestParam(required = false, defaultValue = "7") Integer leadTimeDays) {
        try {
            PredictionClientResponse forecast = demandPredictionService.forecastProductDemand(
                    productId, discountPercent, leadTimeDays);
            return ResponseEntity.ok(forecast);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    @GetMapping("/forecast/history/{productId}")
    public ResponseEntity<List<DemandForecast>> getForecastHistory(@PathVariable Long productId) {
        return ResponseEntity.ok(demandPredictionService.getForecastHistory(productId));
    }

    @GetMapping("/forecast/reorder-alerts")
    public ResponseEntity<List<DemandForecast>> getReorderAlerts() {
        return ResponseEntity.ok(demandPredictionService.getActiveReorderRecommendations());
    }
}
