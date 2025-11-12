package com.yallatawsil.backend.config;

import com.yallatawsil.backend.service.distance.DistanceCalculator;
import com.yallatawsil.backend.service.distance.HaversineDistanceCalculator;
import com.yallatawsil.backend.service.optimizer.AIOptimizer;
import com.yallatawsil.backend.service.optimizer.ClarkeWrightOptimizer;
import com.yallatawsil.backend.service.optimizer.NearestNeighborOptimizer;
import com.yallatawsil.backend.service.optimizer.TourOptimizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Application Configuration
 * Configures beans for distance calculation and tour optimization algorithms
 */
@Configuration
@Slf4j
public class AppConfig {

    /**
     * Distance Calculator Bean
     * Uses Haversine formula for GPS coordinate distance calculation
     */
    @Bean
    public DistanceCalculator distanceCalculator() {
        log.info("Initializing DistanceCalculator (Haversine Formula)");
        return new HaversineDistanceCalculator();
    }

    /**
     * Nearest Neighbor Optimizer Bean
     * Greedy algorithm: selects the closest next delivery point
     * - Fast execution
     * - Good for small datasets
     * - May not find the optimal solution
     */
    @Bean
    public NearestNeighborOptimizer nearestNeighborOptimizer(DistanceCalculator distanceCalculator) {
        log.info("Initializing NearestNeighborOptimizer");
        return new NearestNeighborOptimizer(distanceCalculator);
    }

    /**
     * Clarke-Wright Optimizer Bean
     * Savings algorithm: merges routes to minimize total distance
     * - Better optimization than Nearest Neighbor
     * - Moderate execution time
     * - Good balance between speed and quality
     */
    @Bean
    public ClarkeWrightOptimizer clarkeWrightOptimizer(DistanceCalculator distanceCalculator) {
        log.info("Initializing ClarkeWrightOptimizer");
        return new ClarkeWrightOptimizer(distanceCalculator);
    }

    /**
     * Optimizer Strategy Map
     * Maps algorithm names to their implementations
     * Available Algorithms:
     * - NEAREST_NEIGHBOR: Fast, greedy approach
     * - CLARKE_WRIGHT: Better optimization with savings algorithm
     * - AI_OPTIMIZER: AI-powered optimization (conditionally enabled)
     *
     * @param nearestNeighborOptimizer Nearest Neighbor implementation
     * @param clarkeWrightOptimizer Clarke-Wright implementation
     * @param aiOptimizer AI Optimizer (optional, injected if enabled)
     * @return Map of algorithm name -> optimizer instance
     */
    @Bean
    public Map<String, TourOptimizer> optimizers(
            NearestNeighborOptimizer nearestNeighborOptimizer,
            ClarkeWrightOptimizer clarkeWrightOptimizer,
            @Autowired(required = false) AIOptimizer aiOptimizer
    ) {
        log.info("Initializing Optimizer Strategy Map");

        Map<String, TourOptimizer> optimizersMap = new HashMap<>();

        // Always available optimizers
        optimizersMap.put("NEAREST_NEIGHBOR", nearestNeighborOptimizer);
        optimizersMap.put("CLARKE_WRIGHT", clarkeWrightOptimizer);

        // Conditionally add AI Optimizer if enabled
        if (aiOptimizer != null) {
            optimizersMap.put("AI_OPTIMIZER", aiOptimizer);
            log.info("AI Optimizer ENABLED");
            log.info("   - Analyzes historical delivery patterns");
            log.info("   - Uses Spring AI with OpenAI GPT models");
            log.info("   - Provides intelligent route recommendations");
        } else {
            log.info("AI Optimizer DISABLED");
            log.info("To enable: set 'ai.optimizer.enabled=true' in application.yml");
            log.info("Ensure OPENAI_API_KEY environment variable is set");
        }

        log.info("Available optimization algorithms: {}", optimizersMap.keySet());
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return optimizersMap;
    }
}