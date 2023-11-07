package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.dto.EmployeeFindDto;
import com.mexico.sas.admin.api.dto.EmployeeFindSelectDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Client;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.repository.EmployeeRepository;
import com.mexico.sas.admin.api.service.EmployeeService;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl extends Utils implements EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    @Override
    public EmployeeFindDto findById(Long id) throws CustomException {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.EMPLOYEE_NOT_FOUND, id)));
        return from_M_To_N(employee, EmployeeFindDto.class);
    }

    @Override
    public EmployeeFindDto findByClientAndId(Long clientId, Long id) throws CustomException {
        Employee employee = repository.findByClientAndId(new Client(clientId), id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.EMPLOYEE_NOT_FOUND, id)));
        return from_M_To_N(employee, EmployeeFindDto.class);
    }

    @Override
    public List<EmployeeFindSelectDto> getForSelect(Long clientId) {
        List<Employee> employees = repository.findByClientAndActiveIsTrueAndEliminateIsFalse(new Client(clientId));
        List<EmployeeFindSelectDto> employeesFindSelectDto = new ArrayList<>();
        employees.forEach( employee -> {
            try {
                EmployeeFindSelectDto employeeFindSelectDto = from_M_To_N(employee, EmployeeFindSelectDto.class);
                employeeFindSelectDto.setName(buildFullname(employee.getName(), employee.getSecondName(), employee.getSurname(), employee.getSecondSurname()));
                employeesFindSelectDto.add(employeeFindSelectDto);
            } catch (CustomException e2) {
                log.error("Impossible add employee {}", employee.getId());
            }
        });
        return employeesFindSelectDto;
    }
}
