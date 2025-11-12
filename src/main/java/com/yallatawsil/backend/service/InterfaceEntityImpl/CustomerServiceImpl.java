package com.yallatawsil.backend.service.InterfaceEntityImpl;

import com.yallatawsil.backend.dto.request.CustomerRequestDTO;
import com.yallatawsil.backend.dto.response.CustomerResponseDTO;
import com.yallatawsil.backend.entity.Customer;
import com.yallatawsil.backend.entity.enums.DeliveryStatus;
import com.yallatawsil.backend.mapper.CustomerMapper;
import com.yallatawsil.backend.repository.CustomerRepository;
import com.yallatawsil.backend.service.BaseServiceImpl.BaseServiceImpl;
import com.yallatawsil.backend.service.InterfaceEntity.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CustomerServiceImpl extends BaseServiceImpl<Customer, CustomerRequestDTO, CustomerResponseDTO, Long>
        implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        super(customerRepository, "Customer");
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    protected Customer toEntity(CustomerRequestDTO dto) {
        return customerMapper.toEntity(dto);
    }

    @Override
    protected CustomerResponseDTO toResponseDTO(Customer customer) {
        return enrichResponseDTO(customer);
    }

    @Override
    protected void updateEntityFromDTO(CustomerRequestDTO dto, Customer customer) {
        log.debug("Updating entity: {}", customer.getName());

        if (!customer.getName().equals(dto.getName())
                && customerRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Customer with name " + dto.getName() + " already exists");
        }

        customer.setName(dto.getName());
        customer.setAddress(dto.getAddress());
        customer.setLatitude(dto.getLatitude());
        customer.setLongitude(dto.getLongitude());
        customer.setPreferredTimeSlot(dto.getPreferredTimeSlot());
    }

    @Override
    protected Long getEntityId(Customer customer) {
        return customer.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> findAll(Pageable pageable) {
        log.debug("Finding all customers with pagination");
        return customerRepository.findAll(pageable).map(this::enrichResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> searchByName(String name) {
        log.debug("Searching customers by name: {}", name);
        return customerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> searchByAddress(String address) {
        log.debug("Searching customers by address: {}", address);
        return customerRepository.findByAddressContainingIgnoreCase(address).stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> findCustomersWithinRadius(Double latitude, Double longitude, Double radiusKm) {
        log.debug("Finding customers within {} km of ({}, {})", radiusKm, latitude, longitude);
        return customerRepository.findCustomersWithinRadius(latitude, longitude, radiusKm).stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> findCustomersWithPendingDeliveries() {
        log.debug("Finding customers with pending deliveries");
        return customerRepository.findCustomersWithPendingDeliveries().stream()
                .map(this::enrichResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Enrich ResponseDTO with computed statistics
     * Business logic lives here (not in mapper)
     */
    private CustomerResponseDTO enrichResponseDTO(Customer customer) {
        CustomerResponseDTO dto = customerMapper.toResponseDTO(customer);

        if (customer.getDeliveries() != null) {
            dto.setTotalDeliveries((long) customer.getDeliveries().size());
            dto.setPendingDeliveries(customer.getDeliveries().stream()
                    .filter(d -> d.getStatus() == DeliveryStatus.PENDING)
                    .count());
            dto.setCompletedDeliveries(customer.getDeliveries().stream()
                    .filter(d -> d.getStatus() == DeliveryStatus.DELIVERED)
                    .count());
        } else {
            dto.setTotalDeliveries(0L);
            dto.setPendingDeliveries(0L);
            dto.setCompletedDeliveries(0L);
        }

        return dto;
    }
}