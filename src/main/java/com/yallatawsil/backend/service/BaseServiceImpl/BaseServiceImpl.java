package com.yallatawsil.backend.service.BaseServiceImpl;

import com.yallatawsil.backend.exception.ResourceNotFoundException;
import com.yallatawsil.backend.service.BaseService.BaseService;
import org.springframework.transaction.annotation.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter

public abstract class BaseServiceImpl<Entity, RequestDTO, ResponseDTO, ID>
        implements BaseService<RequestDTO, ResponseDTO, ID> {

    protected final JpaRepository<Entity, ID> repository;
    protected final String entityName;

    protected BaseServiceImpl(JpaRepository<Entity, ID> repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    protected abstract Entity toEntity(RequestDTO dto);

    protected abstract ResponseDTO toResponseDTO(Entity entity);

    protected abstract void updateEntityFromDTO(RequestDTO dto, Entity entity);

    protected abstract ID getEntityId(Entity entity);

    @Override
    public ResponseDTO create(RequestDTO dto) {
        log.debug("Creating {}", entityName);

        Entity entity = toEntity(dto);
        Entity saved = repository.save(entity);

        log.info("{} created with id: {}", entityName, getEntityId(saved));
        return toResponseDTO(saved);
    }

    @Override
    public ResponseDTO findById(ID id) {
        log.debug("Finding {} by id: {}", entityName, id);

        Entity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with id: " + id));

        return toResponseDTO(entity);
    }

    @Override
    public List<ResponseDTO> findAll() {
        log.debug("Finding all {}", entityName);

        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResponseDTO> findAll(Pageable pageable){
        log.debug("Find all with pagination");

        return repository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Override
    public ResponseDTO update(ID id, RequestDTO dto) {
        log.debug("Updating {} with id: {}", entityName, id);

        Entity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with id: " + id));

        updateEntityFromDTO(dto, entity);
        Entity updated = repository.save(entity);

        log.info("{} updated with id: {}", entityName, id);
        return toResponseDTO(updated);
    }

    @Override
    public void delete(ID id) {
        log.debug("Deleting {} with id: {}", entityName, id);

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(entityName + " not found with id: " + id);
        }

        repository.deleteById(id);
        log.info("{} deleted with id: {}", entityName, id);
    }


}