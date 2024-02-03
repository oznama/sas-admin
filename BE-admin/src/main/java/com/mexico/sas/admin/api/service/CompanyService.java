package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.company.*;
import com.mexico.sas.admin.api.dto.project.ProjectFindDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {

    CompanyFindDto save(CompanyDto companyDto) throws CustomException;
    CompanyUpdateDto update(Long id, CompanyUpdateDto companyUpdateDto) throws CustomException;
    void deleteLogic(Long id) throws CustomException;
    void delete(Long id) throws CustomException;
    Page<CompanyPaggeableDto> findAll(String filter, Long type, Pageable pageable);

    CompanyFindDto findByRfc(String rfc) throws CustomException;
    CompanyFindDto findById(Long id) throws CustomException;
    Company findEntityById(Long id) throws CustomException;
    List<CompanyFindSelectDto> getForSelect();
}
