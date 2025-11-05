package com.yallatawsil.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequestDTO {

    @NotBlank(message = "Delivery address is required")
    private String address;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Latitude is required")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    private Double weight;

    @NotNull(message = "Volume is required")
    @DecimalMin(value = "0.01", message = "Volume must be greater than 0")
    private Double volume;

    private String preferredTimeSlot;

    private String notes;

}
