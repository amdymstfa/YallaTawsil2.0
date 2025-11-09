package com.yallatawsil.backend.controller;

import com.yallatawsil.backend.dto.request.TourRequestDTO;
import com.yallatawsil.backend.dto.response.OptimizationComparisonDTO;
import com.yallatawsil.backend.dto.response.TourResponseDTO;
import com.yallatawsil.backend.entity.enums.TourStatus;
import com.yallatawsil.backend.service.InterfaceEntity.TourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Tour", description = "Tour optimization and management APIs")
@AllArgsConstructor
@Slf4j
public class TourController {

    private final TourService tourService;

    @PostMapping("/optimize")
    @Operation(summary = "Optimize a tour with specified algorithm")
    public ResponseEntity<TourResponseDTO> optimizeTour(@Valid @RequestBody TourRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.optimizeTour(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tour by ID")
    public ResponseEntity<TourResponseDTO> getTourById(@PathVariable Long id) {
        return ResponseEntity.ok(tourService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Get all tours")
    public ResponseEntity<List<TourResponseDTO>> getAllTours() {
        return ResponseEntity.ok(tourService.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a tour")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/distance")
    @Operation(summary = "Get total distance of a tour")
    public ResponseEntity<Double> getTotalDistance(@PathVariable Long id) {
        return ResponseEntity.ok(tourService.getTotalDistance(id));
    }

    @PostMapping("/compare")
    @Operation(summary = "Compare Nearest Neighbor vs Clarke & Wright algorithms")
    public ResponseEntity<OptimizationComparisonDTO> compareAlgorithms(@Valid @RequestBody TourRequestDTO dto) {
        return ResponseEntity.ok(tourService.compareAlgorithms(dto));
    }

    /**
     * Update tour status
     * When status changes to COMPLETED, DeliveryHistory records are automatically created
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update tour status",
            description = "Automatically creates delivery history when status changes to COMPLETED")
    public ResponseEntity<TourResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam TourStatus status
    ) {
        log.info("PATCH /api/v1/tours/{}/status?status={}", id, status);
        TourResponseDTO updated = tourService.updateTourStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}