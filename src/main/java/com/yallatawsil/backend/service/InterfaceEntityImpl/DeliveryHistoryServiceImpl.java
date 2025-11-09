package com.yallatawsil.backend.service.InterfaceEntityImpl;

import com.yallatawsil.backend.dto.response.DeliveryHistoryResponseDTO;
import com.yallatawsil.backend.entity.DeliveryHistory;
import com.yallatawsil.backend.exception.ResourceNotFoundException;
import com.yallatawsil.backend.mapper.DeliveryHistoryMapper;
import com.yallatawsil.backend.repository.DeliveryHistoryRepository;
import com.yallatawsil.backend.service.InterfaceEntity.DeliveryHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryHistoryServiceImpl implements DeliveryHistoryService {

    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final DeliveryHistoryMapper deliveryHistoryMapper;

    @Override
    public DeliveryHistoryResponseDTO findById(Long id) {
        log.debug("Find delivery history by id {}", id);

        DeliveryHistory history = deliveryHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery history not found with id: " + id));

        return enrichResponseDTO(history);
    }

    @Override
    public List<DeliveryHistoryResponseDTO> findAll() {
        log.debug("Finding all delivery histories");

        return deliveryHistoryRepository.findAll().stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DeliveryHistoryResponseDTO> findAll(Pageable pageable) {
        log.debug("Find all delivery histories with pagination");

        return deliveryHistoryRepository.findAll(pageable)
                .map(this::enrichResponseDTO);
    }

    @Override
    public List<DeliveryHistoryResponseDTO> findByCustomerId(Long customerId) {
        log.debug("Find customer with id: {}", customerId);
        
        return deliveryHistoryRepository.findByCustomerId(customerId).stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryHistoryResponseDTO> findByTourId(Long tourId) {
        log.debug("Finding delivery history for tour: {}", tourId);

        return deliveryHistoryRepository.findByTourId(tourId).stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryHistoryResponseDTO> findByDeliveryId(Long deliveryId) {
        log.debug("Finding delivery history for delivery: {}", deliveryId);

        return deliveryHistoryRepository.findByDeliveryId(deliveryId).stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryHistoryResponseDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Finding delivery history between {} and {}", startDate, endDate);

        return deliveryHistoryRepository.findByDeliveryDateBetween(startDate, endDate).stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryHistoryResponseDTO> findByDayOfWeek(String dayOfWeek) {
        log.debug("Finding delivery history for day: {}", dayOfWeek);

        return deliveryHistoryRepository.findByDayOfWeek(dayOfWeek).stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryHistoryResponseDTO> findDelayedDeliveries() {
        log.debug("Finding delayed deliveries");

        return deliveryHistoryRepository.findDelayedDeliveries().stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAverageDelayByDayOfWeek() {
        log.debug("Calculating average delay by day of week");

        // ✅ IMPLÉMENTATION COMPLÈTE
        List<Object[]> results = deliveryHistoryRepository.getAverageDelayByDayOfWeek();
        Map<String, Object> stats = new HashMap<>();

        for (Object[] row : results) {
            String day = (String) row[0];
            Double avgDelay = (Double) row[1];
            Long total = (Long) row[2];

            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("averageDelayMinutes", avgDelay != null ? avgDelay : 0.0);
            dayStats.put("totalDeliveries", total);

            stats.put(day, dayStats);
        }

        log.info("Average delay stats: {}", stats);
        return stats;
    }

    @Override
    public List<Map<String, Object>> findProblematicZones(Integer thresholdMinutes) {
        log.debug("Finding problematic zones with delay > {} minutes", thresholdMinutes);

        // ✅ IMPLÉMENTATION COMPLÈTE
        List<Object[]> results = deliveryHistoryRepository.findProblematicZones(thresholdMinutes);

        List<Map<String, Object>> zones = results.stream().map(row -> {
            Map<String, Object> zone = new HashMap<>();
            zone.put("address", row[0]);
            zone.put("averageDelayMinutes", row[1]);
            zone.put("totalDeliveries", row[2]);
            return zone;
        }).collect(Collectors.toList());

        log.info("Found {} problematic zones", zones.size());
        return zones;
    }

    /**
     * Enrich ResponseDTO with computed fields
     * Business logic for formatted delay
     */
    private DeliveryHistoryResponseDTO enrichResponseDTO(DeliveryHistory deliveryHistory) {
        // Convert entity to DTO
        DeliveryHistoryResponseDTO dto = deliveryHistoryMapper.toResponseDTO(deliveryHistory);

        // Handle status of delivery
        if (deliveryHistory.getDelayMinutes() != null) {
            if (deliveryHistory.getDelayMinutes() > 0) {
                dto.setDelayStatus("LATE");
            } else if (deliveryHistory.getDelayMinutes() < 0) {
                dto.setDelayStatus("EARLY");
            } else {
                dto.setDelayStatus("ON_TIME");
            }
            // Format delay for display
            dto.setFormattedDelay(formatDelay(deliveryHistory.getDelayMinutes()));
        } else {
            dto.setDelayStatus("UNKNOWN"); // ✅ Correction: UNKNOWN au lieu de UNKNOW
            dto.setFormattedDelay("N/A");
        }

        return dto;
    }

    /**
     * Format delay in minutes to human-readable string
     */
    private String formatDelay(Integer delayMinute) {
        // Print information if missing value
        if (delayMinute == null) {
            return "N/A";
        }

        // Handle the correct format of delayMinute
        int minutes = Math.abs(delayMinute);

        if (delayMinute == 0) {
            return "On time";
        } else if (delayMinute > 0) {
            return minutes + " minute" + (minutes != 1 ? "s" : "") + " late";
        } else {
            return minutes + " minute" + (minutes != 1 ? "s" : "") + " early";
        }
    }
}