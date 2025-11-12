package com.yallatawsil.backend.service.InterfaceEntityImpl;

import com.yallatawsil.backend.dto.request.WarehouseRequestDTO;
import com.yallatawsil.backend.dto.response.WarehouseResponseDTO;
import com.yallatawsil.backend.entity.Warehouse;
import com.yallatawsil.backend.mapper.WarehouseMapper;
import com.yallatawsil.backend.repository.WarehouseRepository;
import com.yallatawsil.backend.service.BaseServiceImpl.BaseServiceImpl;
import com.yallatawsil.backend.service.InterfaceEntity.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@Slf4j
public class WarehouseServiceImpl extends BaseServiceImpl<Warehouse, WarehouseRequestDTO, WarehouseResponseDTO, Long>
        implements WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        super(warehouseRepository, "Warehouse");
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

//    @Override
//    protected Warehouse toEntity(WarehouseRequestDTO dto) {
//        if (warehouseRepository.existsByName(dto.getName())) {
//            throw new IllegalArgumentException("Warehouse with name " + dto.getName() + " already exists");
//        }
//        return warehouseMapper.toEntity(dto);
//    }
@Override
protected Warehouse toEntity(WarehouseRequestDTO dto) {
    if (warehouseRepository.existsByName(dto.getName())) {
        throw new IllegalArgumentException("Warehouse with name " + dto.getName() + " already exists");
    }

    // Set defaults on DTO if null
    if (dto.getOpeningTime() == null) {
        dto.setOpeningTime(LocalTime.of(6, 0));
    }
    if (dto.getClosingTime() == null) {
        dto.setClosingTime(LocalTime.of(22, 0));
    }

    // Now map - all fields will have values

    return warehouseMapper.toEntity(dto);
}




    @Override
    protected WarehouseResponseDTO toResponseDTO(Warehouse entity) {
        return warehouseMapper.toResponseDTO(entity);
    }

    @Override
    protected void updateEntityFromDTO(WarehouseRequestDTO dto, Warehouse entity) {
        if (!entity.getName().equals(dto.getName())
                && warehouseRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Warehouse with name " + dto.getName() + " already exists");
        }

        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        if (dto.getOpeningTime() != null) entity.setOpeningTime(dto.getOpeningTime());
        if (dto.getClosingTime() != null) entity.setClosingTime(dto.getClosingTime());
    }

    @Override
    protected Long getEntityId(Warehouse entity) {
        return entity.getId();
    }
}