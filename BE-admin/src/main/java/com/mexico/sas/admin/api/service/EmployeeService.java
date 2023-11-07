package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.EmployeeFindDto;
import com.mexico.sas.admin.api.dto.EmployeeFindSelectDto;
import com.mexico.sas.admin.api.exception.CustomException;

import java.util.List;

public interface EmployeeService {
    EmployeeFindDto findById(Long id) throws CustomException;

    EmployeeFindDto findByClientAndId(Long clientId, Long id) throws CustomException;

    List<EmployeeFindSelectDto> getForSelect(Long clientId);
}
