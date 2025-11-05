package com.yallatawsil.backend.mapper ;

import com.yallatawsil.backend.dto.request.CustomerRequestDTO;
import com.yallatawsil.backend.dto.response.CustomerResponseDTO;
import org.mapstruct.Mapper;
import com.yallatawsil.backend.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // Convert DTO to Entity
    CustomerResponseDTO toResponseDTO(Customer customer);
    // Convert Entity to DTO
    Customer toEntity(CustomerRequestDTO dto);
}