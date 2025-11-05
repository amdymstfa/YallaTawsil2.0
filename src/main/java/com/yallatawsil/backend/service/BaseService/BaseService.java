package com.yallatawsil.backend.service.BaseService ;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseService<RequestDTO, ResponseDTO, ID> {

    ResponseDTO create(RequestDTO dto);

    ResponseDTO findById(ID id);

    List<ResponseDTO> findAll();

    Page<ResponseDTO> findAll(Pageable pageable);

    ResponseDTO update(ID id ,RequestDTO dto);

    void delete(ID id);

}