    package com.yallatawsil.backend.dto.response;

    import com.yallatawsil.backend.entity.enums.DeliveryStatus;
    import lombok.*;

    import java.time.LocalDateTime;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class DeliveryResponseDTO {

        private Long id;

        private Long customerId;
        private String customerName;

        private String address;
        private Double latitude;
        private Double longitude;
        private String preferredTimeSlot;

        private Double weight;
        private Double volume;
        private DeliveryStatus status;
        private String notes;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }