package com.yallatawsil.backend.mapper ;

import com.yallatawsil.backend.dto.request.CustomerRequestDTO;
import com.yallatawsil.backend.dto.response.DeliveryHistoryResponseDTO;
import org.mapstruct.Mapper;
import com.yallatawsil.backend.entity.DeliveryHistory;

@Mapper(componentModel = "spring")
public interface DeliveryHistoryMapper {

    // Convert DTO to Entity
    DeliveryHistoryResponseDTO toResponseDTO(DeliveryHistory deliveryHistory);
    // Convert Entity to DTO
    DeliveryHistory toEntity(CustomerRequestDTO dto);
}