package com.yallatawsil.backend.service.InterfaceEntityImpl;

import com.yallatawsil.backend.dto.request.VehicleRequestDTO;
import com.yallatawsil.backend.dto.response.VehicleResponseDTO;
import com.yallatawsil.backend.entity.Vehicle;
import com.yallatawsil.backend.mapper.VehicleMapper;
import com.yallatawsil.backend.repository.VehicleRepository;
import com.yallatawsil.backend.service.BaseServiceImpl.BaseServiceImpl;
import com.yallatawsil.backend.service.InterfaceEntity.VehicleService;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class VehicleServiceImpl extends BaseServiceImpl<Vehicle, VehicleRequestDTO, VehicleResponseDTO, Long>
        implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        super(vehicleRepository, "Vehicle");
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    protected Vehicle toEntity(VehicleRequestDTO dto) {
        return vehicleMapper.toEntity(dto);
    }

    @Override
    protected VehicleResponseDTO toResponseDTO(Vehicle vehicle) {
        return vehicleMapper.toResponseDTO(vehicle);
    }


    @Override
    protected void updateEntityFromDTO(VehicleRequestDTO dto, Vehicle vehicle) {
        vehicle.setType(dto.getType());
        vehicle.setMaxWeight(dto.getMaxWeight());
        vehicle.setMaxVolume(dto.getMaxVolume());
        vehicle.setMaxDeliveries(dto.getMaxDeliveries());
        vehicle.setLicensePlate(dto.getLicensePlate());
    }

    @Override
    protected Long getEntityId(Vehicle vehicle) {
        return vehicle.getId();
    }

    @Override
    public VehicleResponseDTO toResponseDTO(Entity entity) {
        return vehicleMapper.toResponseDTO((Vehicle) entity);
    }

    @Override
    public List<VehicleResponseDTO> findAvailableVehicles(LocalDate date) {
        log.debug("Finding available vehicles for date: {}", date);
        return vehicleRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }
}