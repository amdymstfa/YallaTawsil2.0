package com.yallatawsil.backend.service.InterfaceEntity;

import com.yallatawsil.backend.dto.request.CustomerRequestDTO;
import com.yallatawsil.backend.dto.response.CustomerResponseDTO;
import com.yallatawsil.backend.service.BaseService.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService extends BaseService<CustomerRequestDTO, CustomerResponseDTO, Long> {

    Page<CustomerResponseDTO> findAll(Pageable pageable);
    List<CustomerResponseDTO> searchByName(String name);
    List<CustomerResponseDTO> searchByAddress(String address);
    List<CustomerResponseDTO> findCustomersWithinRadius(Double latitude, Double longitude, Double radiusKm);
    List<CustomerResponseDTO> findCustomersWithPendingDeliveries();

}