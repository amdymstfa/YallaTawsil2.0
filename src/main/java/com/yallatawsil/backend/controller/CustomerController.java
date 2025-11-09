package com.yallatawsil.backend.controller;

import com.yallatawsil.backend.dto.request.CustomerRequestDTO;
import com.yallatawsil.backend.dto.response.CustomerResponseDTO;
import com.yallatawsil.backend.service.InterfaceEntity.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Customer management
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO dto) {
        log.info("POST /api/v1/customers - Creating customer: {}", dto.getName());
        CustomerResponseDTO created = customerService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerResponseDTO> findById(@PathVariable Long id) {
        log.info("GET /api/v1/customers/{}", id);
        CustomerResponseDTO customer = customerService.findById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    @Operation(summary = "Get all customers with pagination")
    public ResponseEntity<Page<CustomerResponseDTO>> findAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        log.info("GET /api/v1/customers - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<CustomerResponseDTO> customers = customerService.findAll(pageable);
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO dto
    ) {
        log.info("PUT /api/v1/customers/{}", id);
        CustomerResponseDTO updated = customerService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer (CASCADE: deletes associated deliveries)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/customers/{}", id);
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search/by-name")
    @Operation(summary = "Search customers by name (case insensitive)")
    public ResponseEntity<List<CustomerResponseDTO>> searchByName(@RequestParam String name) {
        log.info("GET /api/v1/customers/search/by-name?name={}", name);
        List<CustomerResponseDTO> customers = customerService.searchByName(name);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search/by-address")
    @Operation(summary = "Search customers by address (case insensitive)")
    public ResponseEntity<List<CustomerResponseDTO>> searchByAddress(@RequestParam String address) {
        log.info("GET /api/v1/customers/search/by-address?address={}", address);
        List<CustomerResponseDTO> customers = customerService.searchByAddress(address);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search/within-radius")
    @Operation(summary = "Find customers within a radius from a location")
    public ResponseEntity<List<CustomerResponseDTO>> findWithinRadius(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radiusKm
    ) {
        log.info("GET /api/v1/customers/search/within-radius?lat={}&lon={}&radius={}",
                latitude, longitude, radiusKm);
        List<CustomerResponseDTO> customers = customerService.findCustomersWithinRadius(
                latitude, longitude, radiusKm
        );
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/with-pending-deliveries")
    @Operation(summary = "Find customers with pending deliveries")
    public ResponseEntity<List<CustomerResponseDTO>> findCustomersWithPendingDeliveries() {
        log.info("GET /api/v1/customers/with-pending-deliveries");
        List<CustomerResponseDTO> customers = customerService.findCustomersWithPendingDeliveries();
        return ResponseEntity.ok(customers);
    }
}