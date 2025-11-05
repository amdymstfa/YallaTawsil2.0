package com.yallatawsil.backend.service.InterfaceEntityImpl;

import com.yallatawsil.backend.dto.request.TourRequestDTO;
import com.yallatawsil.backend.dto.response.OptimizationComparisonDTO;
import com.yallatawsil.backend.dto.response.TourResponseDTO;
import com.yallatawsil.backend.entity.*;
import com.yallatawsil.backend.exception.ResourceNotFoundException;
import com.yallatawsil.backend.mapper.TourMapper;
import com.yallatawsil.backend.repository.*;
import com.yallatawsil.backend.service.InterfaceEntity.TourService;
import com.yallatawsil.backend.service.distance.DistanceCalculator;
import com.yallatawsil.backend.service.optimizer.TourOptimizer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Setter
@Getter
@Service
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final VehicleRepository vehicleRepository;
    private final WarehouseRepository warehouseRepository;
    private final DeliveryRepository deliveryRepository;
    private final TourMapper tourMapper;
    private final DistanceCalculator distanceCalculator;
    private final Map<String, TourOptimizer> optimizers;

    public TourServiceImpl(TourRepository tourRepository,
                           VehicleRepository vehicleRepository,
                           WarehouseRepository warehouseRepository,
                           DeliveryRepository deliveryRepository,
                           TourMapper tourMapper,
                           DistanceCalculator distanceCalculator,
                           Map<String, TourOptimizer> optimizers) {
        this.tourRepository = tourRepository;
        this.vehicleRepository = vehicleRepository;
        this.warehouseRepository = warehouseRepository;
        this.deliveryRepository = deliveryRepository;
        this.tourMapper = tourMapper;
        this.distanceCalculator = distanceCalculator;
        this.optimizers = optimizers;
    }

    @Override
    public TourResponseDTO optimizeTour(TourRequestDTO dto) {
        log.debug("Optimizing tour with algorithm: {}", dto.getOptimizationAlgorithm());

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        List<Delivery> deliveries = deliveryRepository.findAllById(dto.getDeliveryIds());

        if (deliveries.size() != dto.getDeliveryIds().size()) {
            throw new ResourceNotFoundException("One or more deliveries not found");
        }

        if (tourRepository.isVehicleAssignedOnDate(vehicle, dto.getDate())) {
            throw new IllegalArgumentException("Vehicle already assigned for this date");
        }

        TourOptimizer optimizer = optimizers.get(dto.getOptimizationAlgorithm());
        if (optimizer == null) {
            throw new IllegalArgumentException("Unknown optimization algorithm: " + dto.getOptimizationAlgorithm());
        }

        List<Delivery> orderedDeliveries = optimizer.calculateOptimalTour(warehouse, deliveries, vehicle);
        double totalDistance = optimizer.getTotalDistance(warehouse, orderedDeliveries);

        Tour tour = new Tour();
        tour.setVehicle(vehicle);
        tour.setWarehouse(warehouse);
        tour.setDate(dto.getDate());
        tour.setOptimizationAlgorithm(dto.getOptimizationAlgorithm());
        tour.setTotalDistance(totalDistance);

        for (int i = 0; i < orderedDeliveries.size(); i++) {
            Delivery delivery = orderedDeliveries.get(i);

            double distanceFromPrevious;
            if (i == 0) {
                distanceFromPrevious = distanceCalculator.calculateDistance(
                        warehouse.getLatitude(), warehouse.getLongitude(),
                        delivery.getLatitude(), delivery.getLongitude()
                );
            } else {
                Delivery previous = orderedDeliveries.get(i - 1);
                distanceFromPrevious = distanceCalculator.calculateDistance(
                        previous.getLatitude(), previous.getLongitude(),
                        delivery.getLatitude(), delivery.getLongitude()
                );
            }

            tour.addDelivery(delivery, i + 1, distanceFromPrevious);
        }

        Tour saved = tourRepository.save(tour);

        log.info("Tour created with id: {}, distance: {} km", saved.getId(), totalDistance);
        return tourMapper.toResponseDTO(saved);
    }

    @Override
    public TourResponseDTO findById(Long id) {
        log.debug("Finding tour by id: {}", id);

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + id));

        return tourMapper.toResponseDTO(tour);
    }

    @Override
    public List<TourResponseDTO> findAll() {
        log.debug("Finding all tours");

        return tourRepository.findAll().stream()
                .map(tourMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting tour with id: {}", id);

        if (!tourRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tour not found with id: " + id);
        }

        tourRepository.deleteById(id);
        log.info("Tour deleted with id: {}", id);
    }

    @Override
    public OptimizationComparisonDTO compareAlgorithms(TourRequestDTO dto) {
        log.debug("Comparing optimization algorithms");

        TourRequestDTO nnRequest = new TourRequestDTO(
                dto.getVehicleId(), dto.getWarehouseId(), dto.getDate(),
                dto.getDeliveryIds(), "NEAREST_NEIGHBOR"
        );

        TourRequestDTO cwRequest = new TourRequestDTO(
                dto.getVehicleId(), dto.getWarehouseId(), dto.getDate(),
                dto.getDeliveryIds(), "CLARKE_WRIGHT"
        );

        long nnStart = System.currentTimeMillis();
        TourResponseDTO nnResult = optimizeTour(nnRequest);
        long nnTime = System.currentTimeMillis() - nnStart;

        delete(nnResult.getId());

        long cwStart = System.currentTimeMillis();
        TourResponseDTO cwResult = optimizeTour(cwRequest);
        long cwTime = System.currentTimeMillis() - cwStart;

        OptimizationComparisonDTO comparison = new OptimizationComparisonDTO();
        comparison.setNearestNeighborResult(nnResult);
        comparison.setClarkeWrightResult(cwResult);
        comparison.setNnExecutionTimeMs(nnTime);
        comparison.setCwExecutionTimeMs(cwTime);
        comparison.calculateImprovement();

        log.info("Comparison - NN: {} km in {} ms, CW: {} km in {} ms, Improvement: {}%",
                nnResult.getTotalDistance(), nnTime,
                cwResult.getTotalDistance(), cwTime,
                comparison.getImprovementPercentage());

        return comparison;
    }

    @Override
    public Double getTotalDistance(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + id));

        return tour.getTotalDistance();
    }
}