package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.employee.*;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.LoginException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.repository.EmployeeRepository;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.service.CompanyService;
import com.mexico.sas.admin.api.service.EmployeeService;
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl extends LogMovementUtils implements EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CatalogService catalogService;

    @Override
    public EmployeeFindDto save(EmployeeDto employeeDto) throws CustomException {
        Employee employee = from_M_To_N(employeeDto, Employee.class);
        validationSave(employeeDto, employee);
        repository.save(employee);
        EmployeeFindDto employeeFindDto = from_M_To_N(employeeDto, EmployeeFindDto.class);
        employeeFindDto.setId(employee.getId());
        save(Employee.class.getSimpleName(), employee.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
        return employeeFindDto;
    }

    @Override
    public EmployeeUpdateDto update(Long id, EmployeeUpdateDto employeeDto) throws CustomException {
        Employee employee = findEntityById(id);
        employeeDto.setId(id);
        String message = ChangeBeanUtils.checkEmployee(employee, employeeDto, catalogService, this);

        if(!message.isEmpty()) {
            repository.save(employee);
            save(Employee.class.getSimpleName(), employee.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
        }
        return employeeDto;
    }

    @Override
    public void deleteLogic(Long id) throws CustomException {
        log.debug("Delete logic: {}", id);
        Employee employee = findEntityById(id);
        repository.deleteLogic(id, !employee.getEliminate(), employee.getEliminate());
        save(Project.class.getSimpleName(), id,
                !employee.getEliminate() ? CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS,
                I18nResolver.getMessage(!employee.getEliminate() ? I18nKeys.LOG_GENERAL_DELETE : I18nKeys.LOG_GENERAL_REACTIVE));
    }

    @Override
    public void delete(Long id) throws CustomException {
        findEntityById(id);
        try{
            repository.deleteById(id);
            save(Employee.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE, "TODO");
        } catch (Exception e) {
            throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_DELETED, id));
        }
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
    public Page<EmployeePaggeableDto> findAll(String filter, Long companyId, Pageable pageable) {
        Page<Employee> employees = findByFilter(filter, companyId, null, null, pageable);
        List<EmployeePaggeableDto> employeeFindDtos = new ArrayList<>();
        employees.forEach( employee -> {
            try {
                employeeFindDtos.add(parseEmployeePaggeableDto(employee));
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        } );
        return new PageImpl<>(employeeFindDtos, pageable, employees.getTotalElements());
    }

    @Override
    public EmployeeFindDto findByCompanyAndId(Long companyId, Long id) throws CustomException {
        log.debug("Finding by company {} and id {}", companyId, id);
        Employee employee = repository.findByCompanyIdAndIdAndActiveIsTrueAndEliminateIsFalse(companyId, id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.EMPLOYEE_NOT_FOUND, id)));
        return from_M_To_N(employee, EmployeeFindDto.class);
    }

    @Override
    public List<SelectDto> getForSelect() {
        return getForSelect(getCurrentUser().getCompanyId(), bossesAndPmPositions());
    }

    @Override
    public List<SelectDto> getForSelect(Long companyId, List<Long> positionIds) {
        return getSelect(repository.findByCompanyIdAndPositionIdNotInAndActiveIsTrueAndEliminateIsFalseOrderByNameAscSecondNameAscSurnameAscSecondSurnameAsc(companyId, positionIds));
    }

    @Override
    public List<SelectDto> getForSelect(Long companyId, Long positionId) {
        return getSelect(repository.findByCompanyIdAndPositionIdAndActiveIsTrueAndEliminateIsFalseOrderByNameAscSecondNameAscSurnameAscSecondSurnameAsc(companyId, positionId));
    }

    @Override
    public List<SelectDto> getForSelect(Long companyId) {
        return getSelect(repository.findByCompanyIdAndIdNotInOrderByNameAscSecondNameAscSurnameAscSecondSurnameAsc(companyId, employessNotIn()));
    }

    private EmployeeFindDto parseEmployeeFindDto(Employee employee) throws CustomException {
        EmployeeFindDto employeeFindDto = from_M_To_N(employee, EmployeeFindDto.class);
        return employeeFindDto;
    }

    private EmployeePaggeableDto parseEmployeePaggeableDto(Employee employee) throws CustomException {
        EmployeePaggeableDto employeeFindDto = from_M_To_N(employee, EmployeePaggeableDto.class);
        employeeFindDto.setFullName(buildFullname(employee));
        employeeFindDto.setCompany(companyService.findEntityById(employee.getCompanyId()).getName());
        if( employee.getBossId() != null ) {
            employeeFindDto.setBoss(buildFullname(findEntityById(employee.getBossId())));
        }
        if( employee.getPositionId() != null ) {
            employeeFindDto.setPosition(catalogService.findEntityById(employee.getPositionId()).getValue());
        }
        employeeFindDto.setCreatedBy(buildFullname(findEntityById(employee.getCreatedBy())));
        employeeFindDto.setCreationDate(dateToString(employee.getCreationDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return employeeFindDto;
    }

    private List<SelectDto> getSelect(List<Employee> employees) {
        List<SelectDto> employeesFindSelectDto = new ArrayList<>();
        employees.forEach( employee -> {
            try {
                SelectDto selectDto = from_M_To_N(employee, SelectDto.class);
                selectDto.setName(buildFullname(employee));
                employeesFindSelectDto.add(selectDto);
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

    private Page<Employee> findByFilter(String filter, Long companyId, Long createdBy, Boolean active,
        Pageable pageable) {
        return repository.findAll((Specification<Employee>) (root, query, criteriaBuilder) ->
                getPredicateDinamycFilter(filter, companyId, createdBy, active, criteriaBuilder, root), pageable);
    }

    private Long countByFilter(String filter, Long companyId, Long createdBy, Boolean active) {
        return repository.count((Specification<Employee>) (root, query, criteriaBuilder) ->
                getPredicateDinamycFilter(filter, companyId, createdBy, active, criteriaBuilder, root));
    }

    private Predicate getPredicateDinamycFilter(String filter, Long companyId, Long createdBy, Boolean active,
                                                CriteriaBuilder builder, Root<Employee> root) {
        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(builder.isFalse(root.get(Employee.Fields.eliminate)));
        predicates.add(root.get(Employee.Fields.id).in(employessNotIn()).not());

        if(!StringUtils.isEmpty(filter)) {
            String patternFilter = String.format(GeneralKeys.PATTERN_LIKE, filter.toLowerCase());
            Predicate pName = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Employee.Fields.name))), patternFilter);
            Predicate pSecondName = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Employee.Fields.secondName))), patternFilter);
            Predicate pSurname = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Employee.Fields.surname))), patternFilter);
            Predicate pSecondSurname = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Employee.Fields.secondSurname))), patternFilter);
            predicates.add(builder.or(pName, pSecondName, pSurname, pSecondSurname));
        }

        if(companyId != null) {
            predicates.add(builder.equal(root.get(Employee.Fields.companyId), companyId));
        }

        if(createdBy != null) {
            predicates.add(builder.equal(root.get(Employee.Fields.createdBy), createdBy));
        }

        if(active != null) {
            predicates.add(builder.equal(root.get(Employee.Fields.active), active));
        }

        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
