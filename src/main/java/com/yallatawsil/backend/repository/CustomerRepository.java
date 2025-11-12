package com.yallatawsil.backend.repository;

import com.yallatawsil.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find customers by name (contains, ignore case)
    List<Customer> findByNameContainingIgnoreCase(String name);

    // Find customers by address (contains, ignore case)
    List<Customer> findByAddressContainingIgnoreCase(String address);

    // Find a customer by exact name
    Optional<Customer> findByName(String name);

    // Search customers within a geographic radius
    @Query("SELECT c FROM Customer c " +
            "WHERE SQRT(POWER(c.latitude - :lat, 2) + POWER(c.longitude - :lon, 2)) * 111.32 <= :radiusKm")
    List<Customer> findCustomersWithinRadius(
            @Param("lat") Double latitude,
            @Param("lon") Double longitude,
            @Param("radiusKm") Double radiusKm
    );

    // Find customers with pending deliveries
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.deliveries d WHERE d.status = 'PENDING'")
    List<Customer> findCustomersWithPendingDeliveries();

    // Get customer statistics: total deliveries and completed deliveries
    @Query("SELECT c.id, c.name, COUNT(d) as totalDeliveries, " +
            "SUM(CASE WHEN d.status = 'DELIVERED' THEN 1 ELSE 0 END) as completedDeliveries " +
            "FROM Customer c LEFT JOIN c.deliveries d " +
            "GROUP BY c.id, c.name")
    List<Objects[]> getCustomerStatistics();

    // Check if a customer exists by name
    boolean existsByName(String name);
}
