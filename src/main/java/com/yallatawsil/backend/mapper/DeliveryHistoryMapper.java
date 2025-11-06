package com.yallatawsil.backend.mapper;

import com.yallatawsil.backend.dto.response.DeliveryHistoryResponseDTO;
import com.yallatawsil.backend.entity.DeliveryHistory;
import org.mapstruct.Mapper;

/**
 * DeliveryHistory Mapper
 * Note: DeliveryHistory is created automatically by the system,
 * so there is no RequestDTO or toEntity method
 */
@Mapper(componentModel = "spring")
public interface DeliveryHistoryMapper {

    /**
     * Convert DeliveryHistory entity to DeliveryHistoryResponseDTO
     */
    DeliveryHistoryResponseDTO toResponseDTO(DeliveryHistory deliveryHistory);
}