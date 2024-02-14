package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.application.*;
import com.mexico.sas.admin.api.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {
    ApplicationFindDto save(ApplicationDto applicationDto) throws CustomException;
    ApplicationUpdateDto update(String name, ApplicationUpdateDto applicationUpdateDto) throws CustomException;
    void deleteLogic(String name) throws CustomException;

    Page<ApplicationPaggeableDto> findAll(String filter, Long type, Pageable pageable);
    ApplicationFindDto findByName(String name) throws CustomException;
}
