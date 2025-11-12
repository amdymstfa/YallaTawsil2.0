package com.yallatawsil.backend.mapper;

import com.yallatawsil.backend.dto.request.CustomerRequestDTO;
import com.yallatawsil.backend.dto.response.CustomerResponseDTO;
import com.yallatawsil.backend.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    /**
     * Convert Customer entity to CustomerResponseDTO
     */
    CustomerResponseDTO toResponseDTO(Customer customer);

    /**
     * Convert CustomerRequestDTO to Customer entity
     */
    Customer toEntity(CustomerRequestDTO dto);
}