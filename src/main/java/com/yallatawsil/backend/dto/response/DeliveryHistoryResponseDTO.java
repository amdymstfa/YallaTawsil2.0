package com.yallatawsil.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DeliveryHistory Response DTO
 * Used for returning delivery history data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryHistoryResponseDTO {

    private Long id;

    private Long customerId;
    private Long tourId;
    private Long deliveryId;

    private String customerName;
    private String address;
    private Double latitude;
    private Double longitude;

    // Timing information
    private LocalDate deliveryDate;
    private LocalTime plannedTime;
    private LocalTime actualTime;
    private Integer delayMinutes;
    private String dayOfWeek;

    private LocalDateTime createdAt;

    private String delayStatus;
    private String formattedDelay;
}