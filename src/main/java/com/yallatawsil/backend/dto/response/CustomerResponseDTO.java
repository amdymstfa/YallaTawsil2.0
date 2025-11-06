package com.yallatawsil.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Customer Response DTO
 * Used for returning customer data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {

    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String preferredTimeSlot;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long totalDeliveries;
    private Long pendingDeliveries;
    private Long completedDeliveries;
}