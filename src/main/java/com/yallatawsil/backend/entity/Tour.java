package com.yallatawsil.backend.entity;

import com.yallatawsil.backend.entity.enums.TourStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tour Entity
 * Represents an optimized delivery tour for a specific vehicle and date
 */
@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @NotNull(message = "Warehouse is required")
    private Warehouse warehouse;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceOrder ASC")
    private final List<TourDelivery> tourDeliveries = new ArrayList<>();

    @OneToMany(mappedBy = "tour")
    private List<DeliveryHistory> deliveryHistories = new ArrayList<>();

    @Column(nullable = true)
    private Double totalDistance;

    @Column(length = 50)
    private String optimizationAlgorithm;

    @Column(nullable = true)
    private Integer estimatedDurationMinutes;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(nullable = false, length = 20)
    private TourStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback before persist
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) this.status = TourStatus.PLANNED;
    }

    /**
     * Lifecycle callback before update
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Add a delivery to the tour at a specific sequence
     */
    public void addDelivery(Delivery delivery, int sequenceOrder, double distanceFromPrevious) {
        if (delivery == null) throw new IllegalArgumentException("Delivery cannot be null");
        TourDelivery tourDelivery = new TourDelivery();
        tourDelivery.setTour(this);
        tourDelivery.setDelivery(delivery);
        tourDelivery.setSequenceOrder(sequenceOrder);
        tourDelivery.setDistanceFromPrevious(distanceFromPrevious);
        this.tourDeliveries.add(tourDelivery);
    }

    /**
     * Get an immutable list of ordered deliveries
     */
    public List<Delivery> getOrderedDeliveries() {
        return Collections.unmodifiableList(
                this.tourDeliveries.stream()
                        .sorted(Comparator.comparing(TourDelivery::getSequenceOrder))
                        .map(TourDelivery::getDelivery)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Calculate total weight of all deliveries
     */
    public double getTotalWeight() {
        return getOrderedDeliveries().stream()
                .mapToDouble(Delivery::getWeight)
                .sum();
    }

    /**
     * Calculate total volume of all deliveries
     */
    public double getTotalVolume() {
        return getOrderedDeliveries().stream()
                .mapToDouble(Delivery::getVolume)
                .sum();
    }

    /**
     * Get the number of deliveries in tour
     */
    public int getDeliveryCount() {
        return this.tourDeliveries.size();
    }

    /**
     * Safely update tour status with validation
     */
    public void updateStatus(TourStatus newStatus) {
        if (newStatus == null) throw new IllegalArgumentException("New status cannot be null");
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
    }
}