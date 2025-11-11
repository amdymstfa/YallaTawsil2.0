package com.yallatawsil.backend.repository ;

import com.yallatawsil.backend.entity.Customer;
import com.yallatawsil.backend.entity.Delivery;
import com.yallatawsil.backend.entity.DeliveryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Long> {

    /**
     * List of customer history
     */
    List<DeliveryHistory> findCustomerId(Long customerId);

    /**
     * List of tour by history
     */
    List<DeliveryHistory> findTourId(Long tourId);

    /**
     * List of delivery by history
     */
    List<DeliveryHistory> findDeliveryId(Long deliveryId);

    /**
     * list of dayOfWeek
     */
    List<DeliveryHistory> findByDayOfWeek(String dayOfWeek);

    /**
     * list of period delivery
     */
    List<DeliveryHistory> findPeriodBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Delay delivery
     */
    @Query("SELECT dh FROM DeliveryHistory dh WHERE dh.delayMinutes > 0 ORDER BY dh.delayMinutes DESC")
    List<DeliveryHistory> findDelayDeliveries();


    /**
     * Average static
     */
    @Query("SELECT dh.dayOfWeek, AVG(dh.delayMinutes) as avgDelay, COUNT(dh) as total " +
            "FROM DeliveryHistory dh " +
            "GROUP BY dh.dayOfWeek " +
            "ORDER BY avgDelay DESC")
    List<Objects[]> getAverageDelayByDayOfWeek();

    /**
     * find problematic zone
     */
    @Query("SELECT dh.address, AVG(dh.delayMinutes) as avgDelay, COUNT(dh) as totalDeliveries " +
            "FROM DeliveryHistory dh " +
            "GROUP BY dh.address " +
            "HAVING AVG(dh.delayMinutes) > :thresholdMinutes " +
            "ORDER BY avgDelay DESC")
    List<Objects[]> findProblematicZones(
            @Param("thresholdMinutes") Integer thresholdMinutes
    );

    /**
     * find recent history for AI analysis
     */
    @Query(
            "SELECT dh FRON DeliveryHistory dh" +
                    "WHERE dh.deliveryDate >= :startDate " +
                    "ORDER BY dh.deliveryDate DESC, dh.tour.id, dh.actualTime ASC"
    )
    List<DeliveryHistory> findRecentHistoryForAIAnalysis(@Param("startDate") LocalDate startDate);
}