package com.yallatawsil.backend.repository ;

import com.yallatawsil.backend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by name
     * @param  name of customer
     * @return List of name
     */
    public List<Customer> findByNameContainingIgnoreCase(String name);

    /**
     * find address of customer
     * @param address of customer
     * @return list of address
     */
    public List<Customer> findByAddressContainingIgnoreCase(String address);

    /**
     * Find a exact name
     */
    Optional<Customer> findByName(String name);

    /**
     * Search with geographic area
     */
    @Query("SELECT c FROM Customer c WHERE " +
            "SQRT(POWER(c.latitude - :lat, 2) + POWER(c.longitude - :lon, 2)) * 111.32 <= :radiusKm")
    List<Customer> findCustomersWithinRadius(
            @Param("lat") Double latitude,
            @Param("lon") Double longitude,
            @Param("radiusKm") Double radiusKm
    );

    /**
     * Find customer with pending status of theirs delivery
     */
    @Query("SELECT DISTINCT c FROM Customer c"
         + "JOIN c.delivery d" + "WHERE d.status = 'PENDING'")
    List<Customer> findCustomersWithPendingDeliveries();

    /**
     * Customer static
     */
    @Query("SELECT c.id, c.name, COUNT(d) as totalDeliveries, " +
            "SUM(CASE WHEN d.status = 'DELIVERED' THEN 1 ELSE 0 END) as completedDeliveries " +
            "FROM Customer c LEFT JOIN c.deliveries d " +
            "GROUP BY c.id, c.name")
    List<Objects[]> getCustomerStatistics();

    boolean existsByName(String name);
}