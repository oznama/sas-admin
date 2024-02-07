package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.company.*;
import com.mexico.sas.admin.api.dto.project.ProjectDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Company;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.repository.CompanyRepository;
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
public class CompanyServiceImpl extends LogMovementUtils implements CompanyService {

    @Autowired
    private CompanyRepository repository;
    @Autowired
    private EmployeeService employeeService;

    @Override
    public CompanyFindDto save(CompanyDto companyDto) throws CustomException {
        Company company = from_M_To_N(companyDto, Company.class);
        validationSave(companyDto, company);
        repository.save(company);
        save(Company.class.getSimpleName(), company.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
        CompanyFindDto companyFindDto = from_M_To_N(companyDto, CompanyFindDto.class);
        companyFindDto.setId(company.getId());
        return companyFindDto;
    }

    @Override
    public CompanyUpdateDto update(Long id, CompanyUpdateDto companyUpdateDto) throws CustomException {
        Company company = findEntityById(id);
        companyUpdateDto.setId(id);
        String message = ChangeBeanUtils.checkCompany(company, companyUpdateDto);

        if(!message.isEmpty()) {
            validateRfc(company.getRfc());
            repository.save(company);
            save(Company.class.getSimpleName(), company.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
        }
        return companyUpdateDto;
    }

    @Override
    public void deleteLogic(Long id) throws CustomException {
        log.debug("Delete logic: {}", id);
        Company company = findEntityById(id);
        repository.deleteLogic(id, !company.getEliminate(), company.getEliminate());
        save(Company.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE_LOGIC,
                I18nResolver.getMessage(I18nKeys.LOG_GENERAL_DELETE));
    }

    @Override
    public void delete(Long id) throws CustomException {
        findEntityById(id);
        try{
            repository.deleteById(id);
            save(Company.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE, "TODO");
        } catch (Exception e) {
            throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_DELETED, id));
        }
    }

    @Override
    public Page<CompanyPaggeableDto> findAll(String filter, Long type, Pageable pageable) {
        Page<Company> companies = findByFilter(filter, type, null, null, pageable);
        List<CompanyPaggeableDto> companyPaggeableDtos = new ArrayList<>();
        companies.forEach( company -> {
            try {
                companyPaggeableDtos.add(from_M_To_N(company, CompanyPaggeableDto.class));
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        } );
        return new PageImpl<>(companyPaggeableDtos, pageable, companies.getTotalElements());
    }


    @Override
    public CompanyFindDto findByRfc(String rfc) throws CustomException {
        log.debug("Findind company by RFC: {}", rfc);
        return from_M_To_N(repository.findByRfc(rfc)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.COMPANY_BYRFC_NOT_FOUND, rfc))),
                CompanyFindDto.class);
    }

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
        log.debug("Getting Select Company");
        List<Company> companies = repository.findByActiveIsTrueAndEliminateIsFalse();
        List<CompanyFindSelectDto> companiesSelect = new ArrayList<>();

        if( getCurrentUser().getCompanyId().equals(CatalogKeys.COMPANY_SAS) ) {
            log.debug("The current user is SAS");
            companies.forEach( company -> {
                log.debug("Setting users for {} {}", company.getId(), company.getName());
                try {
                    if( company.getId().equals(CatalogKeys.COMPANY_SAS) ) {
                        log.debug("Getting employees without bosses for SAS");
                        companiesSelect.add(getSelectSingle(company, employeeService.getForSelect(company.getId()/*, bossesPositions()*/)));
                    } else {
                        log.debug("Getting employees for {} {}", company.getId(), company.getName());
                        companiesSelect.add(getSelectSingle(company, employeeService.getForSelect(company.getId(), CatalogKeys.EMPLOYEE_POSITION_PM)));
                    }
                } catch (CustomException e) {
                    log.error("Impossible add company {}", company.getId());
                }
            });
        } else {
            log.debug("The current user is NOT SAS");
            try {
                Company company = findEntityById(getCurrentUser().getCompanyId());
                companiesSelect.add(getSelectSingle(company, employeeService.getForSelect(company.getId(), CatalogKeys.EMPLOYEE_POSITION_PM)));
            } catch (CustomException e) {
                log.error("Impossible add company, {}", e.getMessage());
            }
        }


        return companiesSelect;
    }

    private void validationSave(CompanyDto companyDto, Company company) throws CustomException {
        log.debug("Aqui se esta llamando el validationSave {}", companyDto.getRfc());
        validateRfc(companyDto.getRfc());
        // TODO: Agregar validaciones que se necesiten
        company.setCreatedBy(getCurrentUser().getUserId());
    }

    private void validateRfc(String rfc) throws CustomException {
        try {
            log.debug("Aqui esta en el validateRFC: {}", rfc);
            CompanyFindDto companyDto = findByRfc(rfc);
            log.debug("RFC {} already exist for company {} {}", rfc, companyDto.getId(), companyDto.getName());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.COMPANY_RFC_DUPLICATED, rfc), null);
        } catch (CustomException e) {
            log.error("Validating RFC Exception: {}", e.getMessage());
            if ( e instanceof BadRequestException)
                throw e;
        }
    }

    private CompanyFindSelectDto getSelectSingle(Company company, List<SelectDto> employees) throws CustomException {
        CompanyFindSelectDto companyFindSelectDto = from_M_To_N(company, CompanyFindSelectDto.class);
        companyFindSelectDto.setEmployess(employees);
        return companyFindSelectDto;
    }

    private Page<Company> findByFilter(String filter, Long type, Long createdBy, Boolean active,
                                        Pageable pageable) {
        return repository.findAll((Specification<Company>) (root, query, criteriaBuilder) ->
                getPredicateDinamycFilter(filter, type, createdBy, active, criteriaBuilder, root), pageable);
    }

    private Long countByFilter(String filter, Long type, Long createdBy, Boolean active) {
        return repository.count((Specification<Company>) (root, query, criteriaBuilder) ->
                getPredicateDinamycFilter(filter, type, createdBy, active, criteriaBuilder, root));
    }

    private Predicate getPredicateDinamycFilter(String filter, Long type, Long createdBy, Boolean active,
                                                CriteriaBuilder builder, Root<Company> root) {
        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(builder.isFalse(root.get(Company.Fields.eliminate)));

        if(!StringUtils.isEmpty(filter)) {
            String patternFilter = String.format(GeneralKeys.PATTERN_LIKE, filter.toLowerCase());
            Predicate pName = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Company.Fields.name))), patternFilter);
            Predicate pRfc = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Company.Fields.rfc))), patternFilter);
//            Predicate pAddress = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Company.Fields.address))), patternFilter);
//            Predicate pPhone = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Company.Fields.phone))), patternFilter);
            predicates.add(builder.or(pName, pRfc /*, pAddress, pPhone */ ));
        }

        if(type != null) {
            predicates.add(builder.equal(root.get(Company.Fields.type), type));
        }

        if(createdBy != null) {
            predicates.add(builder.equal(root.get(Company.Fields.createdBy), createdBy));
        }

        if(active != null) {
            predicates.add(builder.equal(root.get(Company.Fields.active), active));
        }

        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

}
