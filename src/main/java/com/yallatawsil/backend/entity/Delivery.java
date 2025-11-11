package com.yallatawsil.backend.entity;

import com.yallatawsil.backend.entity.enums.DeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Delivery Entity
 * Represents a single delivery with GPS coordinates and constraints
 */
@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private Customer customer;

    @OneToMany(mappedBy = "delivery")
    private List<DeliveryHistory> histories = new ArrayList<>();

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    @Column(nullable = false)
    private Double weight;

    @NotNull(message = "Volume is required")
    @DecimalMin(value = "0.01", message = "Volume must be greater than 0")
    @Column(nullable = false)
    private Double volume;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    @Column(length = 1000)
    private String notes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Automatically set creation timestamp and default status
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // Set default status if not provided
        if (this.status == null) {
            this.status = DeliveryStatus.PENDING;
        }

        validateConstraints();
    }

    /**
     * Automatically update timestamp on modification and validate
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        validateConstraints();
    }

    /**
     * Update delivery status with validation
     * @param newStatus the new status to set
     * @throws IllegalStateException if transition is not allowed
     */
    public void updateStatus(DeliveryStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
    }

    /**
     * Validate weight and volume
     */
    private void validateConstraints() {
        if (this.weight != null && this.weight <= 0) {
            throw new IllegalStateException("Weight must be greater than 0");
        }
        if (this.volume != null && this.volume <= 0) {
            throw new IllegalStateException("Volume must be greater than 0");
        }
    }

    /**
     * Get delivery address from associated customer
     */
    public String getAddress() {
        return customer != null ? customer.getAddress() : null;
    }

    /**
     * Get delivery latitude from associated customer
     */
    public Double getLatitude() {
        return customer != null ? customer.getLatitude() : null;
    }

    /**
     * Get delivery longitude from associated customer
     */
    public Double getLongitude() {
        return customer != null ? customer.getLongitude() : null;
    }

    /**
     * Get preferred time slot from associated customer
     */
    public String getPreferredTimeSlot() {
        return customer != null ? customer.getPreferredTimeSlot() : null;
    }
}