package com.yallatawsil.backend.service.InterfaceEntityImpl ;

import com.yallatawsil.backend.dto.request.DeliveryRequestDTO;
import com.yallatawsil.backend.dto.response.DeliveryResponseDTO;
import com.yallatawsil.backend.entity.Customer;
import com.yallatawsil.backend.entity.Delivery;
import com.yallatawsil.backend.entity.enums.DeliveryStatus;
import com.yallatawsil.backend.exception.ResourceNotFoundException;
import com.yallatawsil.backend.mapper.DeliveryMapper;
import com.yallatawsil.backend.repository.CustomerRepository;
import com.yallatawsil.backend.repository.DeliveryRepository;
import com.yallatawsil.backend.service.BaseServiceImpl.BaseServiceImpl;
import com.yallatawsil.backend.service.InterfaceEntity.DeliveryService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Setter
@Getter
@Service
public class DeliveryServiceImpl extends BaseServiceImpl<Delivery, DeliveryRequestDTO, DeliveryResponseDTO, Long>
implements DeliveryService
{
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper ;
    private final CustomerRepository customerRepository;

    public DeliveryServiceImpl(DeliveryRepository deliveryRepository, DeliveryMapper deliveryMapper, CustomerRepository customerRepository){
        super(deliveryRepository, "Delivery");
        this.deliveryRepository = deliveryRepository ;
        this.deliveryMapper = deliveryMapper ;
        this.customerRepository = customerRepository;
    }


    @Override
    protected Delivery toEntity(DeliveryRequestDTO deliveryRequestDTO) {
        return deliveryMapper.toEntity(deliveryRequestDTO);
    }


    @Override
    protected DeliveryResponseDTO toResponseDTO(Delivery delivery) {
        return deliveryMapper.toResponseDTO(delivery);
    }

    @Override
    protected void updateEntityFromDTO(DeliveryRequestDTO dto, Delivery delivery) {
        // Update Customer if changed
        if (!delivery.getCustomer().getId().equals(dto.getCustomerId())) {
            Customer newCustomer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found with id: " + dto.getCustomerId()
                    ));
            delivery.setCustomer(newCustomer);
        }

        delivery.setWeight(dto.getWeight());
        delivery.setVolume(dto.getVolume());
        delivery.setNotes(dto.getNotes());
    }

    @Override
    protected Long getEntityId(Delivery delivery) {
        return delivery.getId();
    }

    public DeliveryResponseDTO updateStatus(Long id, DeliveryStatus newStatus){
        log.debug("Set new status for id : {}", id);

        // Find delivery by id
        Delivery delivery = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Delivery not find with id {}", id));

        // Set new status
        delivery.updateStatus(newStatus);
        Delivery updated = deliveryRepository.save(delivery);

        log.info("Delivery status updated to {} for id: {}", newStatus, id);
        return deliveryMapper.toResponseDTO(updated);
    }

    public List<DeliveryResponseDTO> findByStatus(DeliveryStatus status) {
        log.debug("Finding delivery with status : {}", status);

        return deliveryRepository.findByStatus(status).stream()
                .map(deliveryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponseDTO> findPendingDeliveries() {
        log.debug("Find delivery with status pending");

        return deliveryRepository.findPendingDeliveries().stream()
                .map(deliveryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}