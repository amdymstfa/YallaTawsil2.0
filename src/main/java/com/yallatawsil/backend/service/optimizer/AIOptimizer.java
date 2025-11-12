package com.yallatawsil.backend.service.optimizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yallatawsil.backend.entity.Delivery;
import com.yallatawsil.backend.entity.DeliveryHistory;
import com.yallatawsil.backend.entity.Vehicle;
import com.yallatawsil.backend.entity.Warehouse;
import com.yallatawsil.backend.exception.OptimizationException;
import com.yallatawsil.backend.repository.DeliveryHistoryRepository;
import com.yallatawsil.backend.service.distance.DistanceCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-Powered Tour Optimizer using Spring AI ChatClient
 * Analyzes historical delivery patterns to optimize routes
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "ai.optimizer.enabled", havingValue = "true")
public class AIOptimizer extends TourOptimizer {

    private final DistanceCalculator distanceCalculator;
    private final ChatClient chatClient;
    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final ObjectMapper objectMapper;

    @Value("${ai.optimizer.history-days:90}")
    private int historyDays;

    @Value("${ai.optimizer.min-history-records:10}")
    private int minHistoryRecords;


    public AIOptimizer(DistanceCalculator distanceCalculator,
                       ChatClient.Builder chatClientBuilder,
                       DeliveryHistoryRepository deliveryHistoryRepository) {
        super(distanceCalculator);
        this.distanceCalculator = distanceCalculator;

        this.chatClient = chatClientBuilder
                .defaultSystem("You are an expert logistics optimizer specializing in delivery route optimization.")
                .build();
        this.deliveryHistoryRepository = deliveryHistoryRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries, Vehicle vehicle) {
        log.info("Starting AI optimization for {} deliveries", deliveries.size());

        try {

            LocalDate startDate = LocalDate.now().minusDays(historyDays);
            List<DeliveryHistory> history = deliveryHistoryRepository.findRecentHistoryForAIAnalysis(startDate);

            log.info("📊 Found {} historical records for AI analysis", history.size());


            if (history.size() < minHistoryRecords) {
                log.warn("Not enough history ({} records), falling back to Nearest Neighbor", history.size());
                return fallbackToNearestNeighbor(warehouse, deliveries);
            }


            String prompt = buildOptimizationPrompt(warehouse, deliveries, vehicle, history);
            log.debug("Prompt length: {} characters", prompt.length());


            String aiResponse = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.debug("AI Response: {}", aiResponse);


            List<Delivery> optimizedRoute = parseAIResponse(aiResponse, deliveries);

            log.info("AI optimization completed: {} deliveries ordered", optimizedRoute.size());
            return optimizedRoute;

        } catch (Exception e) {
            log.error("AI optimization failed: {}", e.getMessage(), e);
            log.warn("Falling back to Nearest Neighbor algorithm");
            return fallbackToNearestNeighbor(warehouse, deliveries);
        }
    }

    /**
     * Prompt with IA
     */
    private String buildOptimizationPrompt(Warehouse warehouse, List<Delivery> deliveries,
                                           Vehicle vehicle, List<DeliveryHistory> history) {
        try {
            Map<String, Object> historicalData = analyzeHistory(history);
            List<Map<String, Object>> deliveriesData = deliveries.stream()
                    .map(this::deliveryToMap)
                    .collect(Collectors.toList());

            return String.format("""
                # DELIVERY ROUTE OPTIMIZATION REQUEST
                
                ## Warehouse
                - Name: %s
                - Location: (%.6f, %.6f)
                
                ## Vehicle
                - Type: %s
                - Max Weight: %.2f kg
                - Max Volume: %.2f m³
                - Max Deliveries: %.0f
                
                ## Current Context
                - Date: %s
                - Day: %s
                
                ## Deliveries to Optimize (%d deliveries)
```json
                %s
```
                
                ## Historical Patterns (%d past deliveries)
```json
                %s
```
                
                ## Instructions
                1. Minimize total distance
                2. Respect customer time preferences
                3. Avoid historically problematic zones
                4. Consider day-of-week patterns
                5. Prioritize reliable delivery zones
                
                ## Response Format
                Return ONLY valid JSON (no markdown):
                {
                  "optimizedOrder": [2, 5, 1, 3, 4],
                  "estimatedDistance": 45.5,
                  "recommendations": "Brief explanation",
                  "riskFactors": ["List of potential issues"]
                }
                
                CRITICAL: optimizedOrder must contain ALL delivery IDs: %s
                """,
                    warehouse.getName(),
                    warehouse.getLatitude(), warehouse.getLongitude(),
                    vehicle.getType(),
                    vehicle.getMaxWeight(),
                    vehicle.getMaxVolume(),
                    vehicle.getMaxDeliveries(),
                    LocalDate.now(),
                    LocalDate.now().getDayOfWeek(),
                    deliveries.size(),
                    objectMapper.writeValueAsString(deliveriesData),
                    history.size(),
                    objectMapper.writeValueAsString(historicalData),
                    deliveries.stream().map(Delivery::getId).collect(Collectors.toList())
            );

        } catch (JsonProcessingException e) {
            throw new OptimizationException("Failed to build AI prompt", e);
        }
    }


    private Map<String, Object> analyzeHistory(List<DeliveryHistory> history) {
        Map<String, Object> analysis = new HashMap<>();


        Map<String, Double> avgDelayByDay = history.stream()
                .filter(h -> h.getDelayMinutes() != null)
                .collect(Collectors.groupingBy(
                        DeliveryHistory::getDayOfWeek,
                        Collectors.averagingDouble(DeliveryHistory::getDelayMinutes)
                ));
        analysis.put("averageDelayByDayOfWeek", avgDelayByDay);


        Map<String, Long> problematicZones = history.stream()
                .filter(h -> h.getDelayMinutes() != null && h.getDelayMinutes() > 15)
                .collect(Collectors.groupingBy(
                        DeliveryHistory::getAddress,
                        Collectors.counting()
                ));
        analysis.put("problematicZones", problematicZones);


        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeliveries", history.size());
        stats.put("averageDelay", history.stream()
                .filter(h -> h.getDelayMinutes() != null)
                .mapToInt(DeliveryHistory::getDelayMinutes)
                .average()
                .orElse(0.0));
        stats.put("onTimeRate", history.stream()
                .filter(h -> h.getDelayMinutes() != null && h.getDelayMinutes() <= 5)
                .count() * 100.0 / Math.max(history.size(), 1));
        analysis.put("globalStats", stats);

        return analysis;
    }


    private Map<String, Object> deliveryToMap(Delivery delivery) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", delivery.getId());
        map.put("customerId", delivery.getCustomer().getId());
        map.put("customerName", delivery.getCustomer().getName());
        map.put("address", delivery.getCustomer().getAddress());
        map.put("latitude", delivery.getCustomer().getLatitude());
        map.put("longitude", delivery.getCustomer().getLongitude());
        map.put("weight", delivery.getWeight());
        map.put("volume", delivery.getVolume());
        map.put("preferredTimeSlot", delivery.getCustomer().getPreferredTimeSlot());
        return map;
    }


    private List<Delivery> parseAIResponse(String aiResponse, List<Delivery> deliveries) {
        try {

            String cleanJson = aiResponse.trim()
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();


            JsonNode root = objectMapper.readTree(cleanJson);
            JsonNode orderNode = root.get("optimizedOrder");

            if (orderNode == null || !orderNode.isArray()) {
                throw new OptimizationException("Invalid AI response: missing optimizedOrder");
            }


            Map<Long, Delivery> deliveryMap = deliveries.stream()
                    .collect(Collectors.toMap(Delivery::getId, d -> d));


            List<Delivery> orderedDeliveries = new ArrayList<>();
            for (JsonNode idNode : orderNode) {
                Long id = idNode.asLong();
                Delivery delivery = deliveryMap.get(id);
                if (delivery != null) {
                    orderedDeliveries.add(delivery);
                }
            }


            if (orderedDeliveries.size() != deliveries.size()) {
                log.warn("AI response incomplete: {} returned, {} expected",
                        orderedDeliveries.size(), deliveries.size());


                deliveries.stream()
                        .filter(d -> !orderedDeliveries.contains(d))
                        .forEach(orderedDeliveries::add);
            }


            if (root.has("recommendations")) {
                log.info("AI Recommendations: {}", root.get("recommendations").asText());
            }
            if (root.has("riskFactors")) {
                log.warn("Risk Factors: {}", root.get("riskFactors"));
            }

            return orderedDeliveries;

        } catch (Exception e) {
            throw new OptimizationException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }


    private List<Delivery> fallbackToNearestNeighbor(Warehouse warehouse, List<Delivery> deliveries) {
        List<Delivery> result = new ArrayList<>();
        Set<Delivery> remaining = new HashSet<>(deliveries);

        double currentLat = warehouse.getLatitude();
        double currentLon = warehouse.getLongitude();

        while (!remaining.isEmpty()) {
            Delivery nearest = null;
            double minDistance = Double.MAX_VALUE;

            for (Delivery delivery : remaining) {
                double distance = distanceCalculator.calculateDistance(
                        currentLat, currentLon,
                        delivery.getLatitude(), delivery.getLongitude()
                );
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = delivery;
                }
            }

            if (nearest != null) {
                result.add(nearest);
                remaining.remove(nearest);
                currentLat = nearest.getLatitude();
                currentLon = nearest.getLongitude();
            }
        }

        return result;
    }

    @Override
    public double getTotalDistance(Warehouse warehouse, List<Delivery> orderedDeliveries) {
        if (orderedDeliveries.isEmpty()) return 0.0;

        double total = 0.0;

        // Warehouse -> First
        Delivery first = orderedDeliveries.get(0);
        total += distanceCalculator.calculateDistance(
                warehouse.getLatitude(), warehouse.getLongitude(),
                first.getLatitude(), first.getLongitude()
        );

        // Between deliveries
        for (int i = 1; i < orderedDeliveries.size(); i++) {
            Delivery prev = orderedDeliveries.get(i - 1);
            Delivery curr = orderedDeliveries.get(i);
            total += distanceCalculator.calculateDistance(
                    prev.getLatitude(), prev.getLongitude(),
                    curr.getLatitude(), curr.getLongitude()
            );
        }

        // Last -> Warehouse
        Delivery last = orderedDeliveries.getLast();
        total += distanceCalculator.calculateDistance(
                last.getLatitude(), last.getLongitude(),
                warehouse.getLatitude(), warehouse.getLongitude()
        );

        return total;
    }
}