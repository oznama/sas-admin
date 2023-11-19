package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.company.CompanyFindDto;
import com.mexico.sas.admin.api.dto.company.CompanyFindSelectDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Company;

import java.util.List;

public interface CompanyService {
    CompanyFindDto findById(Long id) throws CustomException;
    Company findEntityById(Long id) throws CustomException;
    List<CompanyFindSelectDto> getForSelect();
}
