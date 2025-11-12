package com.yallatawsil.backend.repository;

import com.yallatawsil.backend.entity.Delivery;
import com.yallatawsil.backend.entity.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Delivery Repository
 * Data access layer for Delivery entity
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // Find all deliveries with a specific status
    List<Delivery> findByStatus(DeliveryStatus status);

    // Find all pending deliveries (not yet assigned to a tour)
    @Query("SELECT d FROM Delivery d WHERE d.status = 'PENDING'")
    List<Delivery> findPendingDeliveries();

    // Find deliveries created within a date range
    @Query("SELECT d FROM Delivery d WHERE d.createdAt BETWEEN :start AND :end")
    List<Delivery> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Find deliveries by status and date range
    @Query("SELECT d FROM Delivery d WHERE d.status = :status AND d.createdAt BETWEEN :start AND :end")
    List<Delivery> findByStatusAndDateRange(
            @Param("status") DeliveryStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Find deliveries within a geographical area based on customer location
    @Query("SELECT d FROM Delivery d WHERE " +
            "d.customer.latitude BETWEEN :minLat AND :maxLat AND " +
            "d.customer.longitude BETWEEN :minLon AND :maxLon")
    List<Delivery> findDeliveriesInArea(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon
    );

    // Count deliveries by status
    long countByStatus(DeliveryStatus status);

    // Find deliveries with weight greater than specified value
    List<Delivery> findByWeightGreaterThan(Double weight);

    // Find deliveries with volume greater than specified value
    List<Delivery> findByVolumeGreaterThan(Double volume);

    // Find deliveries that are not assigned to any tour and are pending
    @Query("SELECT d FROM Delivery d WHERE d.status = 'PENDING' AND " +
            "d.id NOT IN (SELECT td.delivery.id FROM TourDelivery td)")
    List<Delivery> findUnassignedPendingDeliveries();

    // Find deliveries by customer ID
    List<Delivery> findByCustomerId(Long customerId);

    // Find deliveries by customer ID and status
    List<Delivery> findByCustomerIdAndStatus(Long customerId, DeliveryStatus status);

    // Find deliveries within weight and volume limits
    @Query("SELECT d FROM Delivery d WHERE d.weight <= :maxWeight AND d.volume <= :maxVolume")
    List<Delivery> findByWeightAndVolume(@Param("maxWeight") Double maxWeight,
                                         @Param("maxVolume") Double maxVolume);
}
