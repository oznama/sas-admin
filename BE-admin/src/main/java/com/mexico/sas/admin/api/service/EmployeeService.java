package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.employee.EmployeeDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindSelectDto;
import com.mexico.sas.admin.api.dto.user.UserDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Employee;

import java.util.List;

public interface EmployeeService {

    EmployeeFindDto save(EmployeeDto employeeDto) throws CustomException;
    void update(EmployeeDto employeeDto) throws CustomException;
    Employee findEntityById(Long id) throws CustomException;
    EmployeeFindDto findById(Long id) throws CustomException;
    Employee findEntityByEmail(String email) throws CustomException;

    EmployeeFindDto findByCompanyAndId(Long companyId, Long id) throws CustomException;

    List<EmployeeFindSelectDto> getForSelect(Boolean developers);
    List<EmployeeFindSelectDto> getForSelect(Long companyId, Boolean developer, List<Long> positionIds);
    List<EmployeeFindSelectDto> getForSelect(Long companyId, Boolean developer, Long positionId);
}
