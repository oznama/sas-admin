package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.company.CompanyFindDto;
import com.mexico.sas.admin.api.dto.company.CompanyFindSelectDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindSelectDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Company;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.repository.CompanyRepository;
import com.mexico.sas.admin.api.service.CompanyService;
import com.mexico.sas.admin.api.service.EmployeeService;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CompanyServiceImpl extends LogMovementUtils implements CompanyService {

    @Autowired
    private CompanyRepository repository;
    @Autowired
    private EmployeeService employeeService;

    @Override
    public CompanyFindDto findById(Long id) throws CustomException {
        return from_M_To_N(findEntityById(id), CompanyFindDto.class);
    }

    @Override
    public Company findEntityById(Long id) throws CustomException {
        return repository.findById(id).orElseThrow(() ->
                new NoContentException(I18nResolver.getMessage(I18nKeys.COMPANY_NOT_FOUND, id)));
    }

    @Override
    public List<CompanyFindSelectDto> getForSelect() {
        List<Company> companies = repository.findByActiveIsTrueAndEliminateIsFalse();
        List<CompanyFindSelectDto> companiesSelect = new ArrayList<>();

        if( getCurrentUser().getCompanyId().equals(CatalogKeys.COMPANY_SAS) ) {
            companies.forEach( company -> {
                try {
                    if( company.getId().equals(CatalogKeys.COMPANY_SAS) ) {
                        getSelectSingle(companiesSelect, company, employeeService.getForSelect(company.getId(), bossesPositions()));
                    } else {
                        getSelectSingle(companiesSelect, company, employeeService.getForSelect(company.getId(), CatalogKeys.EMPLOYEE_POSITION_PM));
                    }
                } catch (CustomException e) {
                    log.error("Impossible add company {}", company.getId());
                }
            });
        } else {
            try {
                Company company = findEntityById(getCurrentUser().getCompanyId());
                getSelectSingle(companiesSelect, company, employeeService.getForSelect(company.getId(), CatalogKeys.EMPLOYEE_POSITION_PM));
            } catch (CustomException e) {
                log.error("Impossible add company, {}", e.getMessage());
            }
        }


        return companiesSelect;
    }

    private void getSelectSingle(List<CompanyFindSelectDto> companiesSelect, Company company, List<EmployeeFindSelectDto> employees) throws CustomException {
        CompanyFindSelectDto companyFindSelectDto = from_M_To_N(company, CompanyFindSelectDto.class);
        companyFindSelectDto.setEmployess(employees);
        companiesSelect.add(companyFindSelectDto);
    }

}
