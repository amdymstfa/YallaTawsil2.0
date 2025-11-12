package com.yallatawsil.backend.repository;

import com.yallatawsil.backend.entity.DeliveryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Long> {

    /**
     * Find delivery history by customer ID
     */
    List<DeliveryHistory> findByCustomerId(Long customerId);

    /**
     * Find delivery history by tour ID
     */
    List<DeliveryHistory> findByTourId(Long tourId);

    /**
     * Find delivery history by delivery ID
     */
    List<DeliveryHistory> findByDeliveryId(Long deliveryId);

    /**
     * Find delivery history by day of week
     */
    List<DeliveryHistory> findByDayOfWeek(String dayOfWeek);

    /**
     * Find delivery history within a date range
     */
    List<DeliveryHistory> findByDeliveryDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find all delayed deliveries (delay > 0), ordered by delay DESC
     */
    @Query("SELECT dh FROM DeliveryHistory dh WHERE dh.delayMinutes > 0 ORDER BY dh.delayMinutes DESC")
    List<DeliveryHistory> findDelayedDeliveries();

    /**
     * Get average delay statistics by day of week
     * Returns: [dayOfWeek, avgDelay, totalCount]
     */
    @Query("SELECT dh.dayOfWeek, AVG(dh.delayMinutes) as avgDelay, COUNT(dh) as total " +
            "FROM DeliveryHistory dh " +
            "GROUP BY dh.dayOfWeek " +
            "ORDER BY avgDelay DESC")
    List<Object[]> getAverageDelayByDayOfWeek();

    /**
     * Find problematic zones (addresses with high average delays)
     * Returns: [address, avgDelay, totalDeliveries]
     */
    @Query("SELECT dh.address, AVG(dh.delayMinutes) as avgDelay, COUNT(dh) as totalDeliveries " +
            "FROM DeliveryHistory dh " +
            "GROUP BY dh.address " +
            "HAVING AVG(dh.delayMinutes) > :thresholdMinutes " +
            "ORDER BY avgDelay DESC")
    List<Object[]> findProblematicZones(@Param("thresholdMinutes") Integer thresholdMinutes);

    /**
     * Find recent delivery history for AI analysis
     * Returns deliveries after a specific date, ordered for AI processing
     */
    @Query("SELECT dh FROM DeliveryHistory dh " +
            "WHERE dh.deliveryDate >= :startDate " +
            "ORDER BY dh.deliveryDate DESC, dh.tour.id, dh.actualTime ASC")
    List<DeliveryHistory> findRecentHistoryForAIAnalysis(@Param("startDate") LocalDate startDate);

    /**
     * Pagination support
     */
    Page<DeliveryHistory> findAll(Pageable pageable);
}