package com.yallatawsil.backend.config ;

import com.yallatawsil.backend.service.distance.DistanceCalculator;
import com.yallatawsil.backend.service.distance.HaversineDistanceCalculator;
import com.yallatawsil.backend.service.optimizer.ClarkeWrightOptimizer;
import com.yallatawsil.backend.service.optimizer.NearestNeighborOptimizer;
import com.yallatawsil.backend.service.optimizer.TourOptimizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig
{
    @Bean
    public DistanceCalculator distanceCalculator(){
        return new HaversineDistanceCalculator();
    }

    @Bean
    public NearestNeighborOptimizer nearestNeighborOptimizer(DistanceCalculator distanceCalculator){
        return new NearestNeighborOptimizer(distanceCalculator);
    }

    @Bean
    public ClarkeWrightOptimizer clarkeWrightOptimizer(DistanceCalculator distanceCalculator){
        return new ClarkeWrightOptimizer(distanceCalculator);
    }

    @Bean
    public Map<String, TourOptimizer> optimizers(
            NearestNeighborOptimizer nn,
            ClarkeWrightOptimizer cw
    ){
        Map<String, TourOptimizer> map = new HashMap<>();
        map.put("NEAREST_NEIGHBOR", nn);
        map.put("CLARKE_WRIGHT", cw);
        return map ;
    }
}