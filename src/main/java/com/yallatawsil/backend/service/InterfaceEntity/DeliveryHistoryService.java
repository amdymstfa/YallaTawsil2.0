package com.yallatawsil.backend.service.InterfaceEntity;

import com.yallatawsil.backend.dto.response.DeliveryHistoryResponseDTO;
import com.yallatawsil.backend.service.BaseService.ReadOnlyBaseService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DeliveryHistory Service Interface
 * Read-only service for historical data
 */
public interface DeliveryHistoryService
        extends ReadOnlyBaseService<DeliveryHistoryResponseDTO, Long> {

    // Searches by entity
    List<DeliveryHistoryResponseDTO> findByCustomerId(Long customerId);
    List<DeliveryHistoryResponseDTO> findByTourId(Long tourId);
    List<DeliveryHistoryResponseDTO> findByDeliveryId(Long deliveryId);

    // Searches by date
    List<DeliveryHistoryResponseDTO> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<DeliveryHistoryResponseDTO> findByDayOfWeek(String dayOfWeek);

    // Analytics
    List<DeliveryHistoryResponseDTO> findDelayedDeliveries();
    Map<String, Object> getAverageDelayByDayOfWeek();
    List<Map<String, Object>> findProblematicZones(Integer thresholdMinutes);
}
