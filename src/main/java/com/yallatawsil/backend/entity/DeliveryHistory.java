package com.yallatawsil.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DeliveryHistory Entity
 * Immutable snapshot of a completed delivery
 * Created automatically when a Tour status changes to COMPLETED
 */
@Entity
@Table(name = "delivery_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private Customer customer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    @NotNull(message = "Tour is required")
    private Tour tour;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    @NotNull(message = "Delivery is required")
    private Delivery delivery;

    /**
     * Snapshot data from Customer
     */
    @NotBlank(message = "Customer name is required")
    @Column(nullable = false)
    private String customerName;

    @NotBlank(message = "Delivery address is required")
    @Column(nullable = false, length = 500)
    private String address;

    @NotNull(message = "Latitude is required")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Column(nullable = false)
    private Double longitude;

    /**
     * Delivery timing information
     */
    @NotNull(message = "Delivery date is required")
    @Column(nullable = false)
    private LocalDate deliveryDate;

    @Column
    private LocalTime plannedTime;

    @Column
    private LocalTime actualTime;

    /**
     * Delay calculation (actualTime - plannedTime in minutes)
     */
    @Column
    private Integer delayMinutes;

    /**
     * Day of week for pattern analysis
     */
    @Column(length = 20)
    private String dayOfWeek;

    /**
     * Audit field
     */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Lifecycle callback: Set creation timestamp and calculate derived fields
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        // Calculate delay automatically
        if (this.plannedTime != null && this.actualTime != null) {
            this.delayMinutes = (int) Duration.between(this.plannedTime, this.actualTime).toMinutes();
        }

        // Extract day of week
        if (this.deliveryDate != null) {
            this.dayOfWeek = this.deliveryDate.getDayOfWeek().name();
        }
    }
}