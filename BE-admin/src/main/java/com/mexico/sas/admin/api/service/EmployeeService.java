package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.employee.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    EmployeeFindDto save(EmployeeDto employeeDto) throws CustomException;
    EmployeeUpdateDto update(Long id, EmployeeUpdateDto employeeUpdateDto) throws CustomException;
    void deleteLogic(Long id);
    void delete(Long id) throws CustomException;
    Employee findEntityById(Long id) throws CustomException;
    EmployeeFindDto findById(Long id) throws CustomException;
    Employee findEntityByEmail(String email) throws CustomException;
    Page<EmployeePaggeableDto> findAll(String filter, Long companyId, Pageable pageable);

    EmployeeFindDto findByCompanyAndId(Long companyId, Long id) throws CustomException;

    List<EmployeeFindSelectDto> getForSelect(Boolean developers);
    List<EmployeeFindSelectDto> getForSelect(Long companyId, Boolean developer, List<Long> positionIds);
    List<EmployeeFindSelectDto> getForSelect(Long companyId, Boolean developer, Long positionId);
    List<EmployeeFindSelectDto> getForSelect(Long companyId);
}
