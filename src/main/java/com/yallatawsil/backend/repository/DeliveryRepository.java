package com.yallatawsil.backend.repository;

import com.yallatawsil.backend.entity.Delivery;
import com.yallatawsil.backend.entity.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Delivery Repository
 * Data access layer for Delivery entity
 */
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    /**
     * Find all deliveries with a specific status
     * @param status the delivery status
     * @return list of deliveries with the specified status
     */
    List<Delivery> findByStatus(DeliveryStatus status);

    /**
     * Find all pending deliveries (not yet assigned to a tour)
     * @return list of pending deliveries
     */
    @Query("SELECT d FROM Delivery d WHERE d.status = 'PENDING'")
    List<Delivery> findPendingDeliveries();

    /**
     * Find deliveries created within a date range
     * @param start start date and time
     * @param end end date and time
     * @return list of deliveries created in the range
     */
    @Query("SELECT d FROM Delivery d WHERE d.createdAt BETWEEN :start AND :end")
    List<Delivery> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Find deliveries by status and date range
     * @param status the delivery status
     * @param start start date and time
     * @param end end date and time
     * @return list of deliveries matching criteria
     */
    @Query("SELECT d FROM Delivery d WHERE d.status = :status AND d.createdAt BETWEEN :start AND :end")
    List<Delivery> findByStatusAndDateRange(
            @Param("status") DeliveryStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Find deliveries within a geographical area
     * @param minLat minimum latitude
     * @param maxLat maximum latitude
     * @param minLon minimum longitude
     * @param maxLon maximum longitude
     * @return list of deliveries in the area
     */
    @Query("SELECT d FROM Delivery d WHERE " +
            "d.latitude BETWEEN :minLat AND :maxLat AND " +
            "d.longitude BETWEEN :minLon AND :maxLon")
    List<Delivery> findDeliveriesInArea(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon
    );

    /**
     * Count deliveries by status
     * @param status the delivery status
     * @return number of deliveries with the specified status
     */
    long countByStatus(DeliveryStatus status);

    /**
     * Find deliveries with weight greater than specified value
     * @param weight minimum weight in kg
     * @return list of deliveries
     */
    List<Delivery> findByWeightGreaterThan(Double weight);

    /**
     * Find deliveries with volume greater than specified value
     * @param volume minimum volume in m³
     * @return list of deliveries
     */
    List<Delivery> findByVolumeGreaterThan(Double volume);

    /**
     * Find deliveries that are not assigned to any tour and are pending
     * @return list of unassigned pending deliveries
     */
    @Query("SELECT d FROM Delivery d WHERE d.status = 'PENDING' AND " +
            "d.id NOT IN (SELECT td.delivery.id FROM TourDelivery td)")
    List<Delivery> findUnassignedPendingDeliveries();

    List<Delivery> findByCustomerId(Long customerId);

    List<Delivery> findByCustomerIdAndStatus(Long customerId, DeliveryStatus status);
    
    @Query("SELECT d FROM Delivery d WHERE d.weight <= :maxWeight AND d.volume <= :maxVolume")
    List<Delivery> findByWeightAndVolume(
            @Param("maxWeight") Double maxWeight,
            @Param("maxVolume") Double maxVolume
    );
}