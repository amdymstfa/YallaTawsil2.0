package com.yallatawsil.backend.service.BaseService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Base interface for read-only services
 */
public interface ReadOnlyBaseService<ResponseDTO, ID> {

    ResponseDTO findById(ID id);

    List<ResponseDTO> findAll();

    Page<ResponseDTO> findAll(Pageable pageable);
}
