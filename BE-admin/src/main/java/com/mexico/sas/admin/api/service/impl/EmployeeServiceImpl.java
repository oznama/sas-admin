package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.employee.EmployeeDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindSelectDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.LoginException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.repository.EmployeeRepository;
import com.mexico.sas.admin.api.service.EmployeeService;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl extends LogMovementUtils implements EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    @Override
    public EmployeeFindDto save(EmployeeDto employeeDto) throws CustomException {
        return null;
    }

    @Override
    public void update(EmployeeDto employeeDto) throws CustomException {

    }

    @Override
    public Employee findEntityById(Long id) throws CustomException {
        return repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.EMPLOYEE_NOT_FOUND, id)));
    }

    @Override
    public EmployeeFindDto findById(Long id) throws CustomException {
        return from_M_To_N(findEntityById(id), EmployeeFindDto.class);
    }

    @Override
    public Employee findEntityByEmail(String email) throws CustomException {
        log.debug("Finding employee with email {} ...", email);
        return repository.findByEmailIgnoreCaseAndActiveIsTrueAndEliminateIsFalse(email).orElseThrow(() ->
                new LoginException(I18nResolver.getMessage(I18nKeys.LOGIN_USER_EMAIL_NO_FOUND)));
    }

    @Override
    public EmployeeFindDto findByCompanyAndId(Long companyId, Long id) throws CustomException {
        log.debug("Finding by company {} and id {}", companyId, id);
        Employee employee = repository.findByCompanyIdAndIdAndActiveIsTrueAndEliminateIsFalse(companyId, id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.EMPLOYEE_NOT_FOUND, id)));
        return from_M_To_N(employee, EmployeeFindDto.class);
    }

    @Override
    public List<EmployeeFindSelectDto> getForSelect(Boolean developers) {
        return getForSelect(getCurrentUser().getCompanyId(), developers, bossesAndPmPositions());
    }

    @Override
    public List<EmployeeFindSelectDto> getForSelect(Long companyId, Boolean developers, List<Long> positionIds) {
        List<Employee> employees = getCurrentUser().getRoleId().equals(GeneralKeys.ROOT_USER_ID)
            ? repository.findAll()
            : (companyId.equals(CatalogKeys.COMPANY_SAS) && !developers
                ? repository.findByPositionIdNotInAndActiveIsTrueAndEliminateIsFalse(positionIds)
                : repository.findByCompanyIdAndPositionIdNotInAndActiveIsTrueAndEliminateIsFalse(companyId, positionIds)
            );
        return getSelect(employees);
    }

    @Override
    public List<EmployeeFindSelectDto> getForSelect(Long companyId, Boolean developers, Long positionId) {
        return getSelect(repository.findByCompanyIdAndPositionIdAndActiveIsTrueAndEliminateIsFalse(companyId, positionId));
    }

    private List<EmployeeFindSelectDto> getSelect(List<Employee> employees) {
        List<EmployeeFindSelectDto> employeesFindSelectDto = new ArrayList<>();
        employees.forEach( employee -> {
            try {
                EmployeeFindSelectDto employeeFindSelectDto = from_M_To_N(employee, EmployeeFindSelectDto.class);
                employeeFindSelectDto.setName(buildFullname(employee));
                employeesFindSelectDto.add(employeeFindSelectDto);
            } catch (CustomException e2) {
                log.error("Impossible add employee {}", employee.getId());
            }
        });
        return employeesFindSelectDto;
    }

    private void validationSave(EmployeeDto employeeDto, Employee employee) throws CustomException {

        // Valiadacion de correo
        try {
            findEntityByEmail(employeeDto.getEmail());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.VALIDATION_EMAIL_DUPLICATED, employeeDto.getEmail()), null);
        } catch (CustomException e) {
            if(e instanceof BadRequestException)
                throw e;
        }
    }
}
