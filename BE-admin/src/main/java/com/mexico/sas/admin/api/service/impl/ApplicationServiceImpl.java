package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.application.*;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Application;
import com.mexico.sas.admin.api.repository.ApplicationRepository;
import com.mexico.sas.admin.api.service.ApplicationService;
import com.mexico.sas.admin.api.service.CompanyService;
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
public class ApplicationServiceImpl extends LogMovementUtils implements ApplicationService {
    @Autowired
    private ApplicationRepository repository;

    @Autowired
    private CompanyService companyService;

    @Override
    public ApplicationFindDto save(ApplicationDto applicationDto) throws CustomException {
        Application application = from_M_To_N(applicationDto, Application.class);
        validationSave(applicationDto, application);
        try {
            repository.save(application);
            return parseFromEntity(application);
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.APPLICATION_NOT_CREATED, applicationDto.getName());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    private ApplicationFindDto parseFromEntity(Application application) throws CustomException {
        ApplicationFindDto applicationFindDto = from_M_To_N(application, ApplicationFindDto.class);
        applicationFindDto.setName(application.getName());
        applicationFindDto.setDescription(application.getDescription());
        applicationFindDto.setCompany(application.getCompany());
        return applicationFindDto;
    }

    private Application parseFromEntity(ApplicationFindDto application) throws CustomException {
        Application applicationFindDto = from_M_To_N(application, Application.class);
        applicationFindDto.setName(application.getName());
        applicationFindDto.setDescription(application.getDescription());
        applicationFindDto.setCompany(application.getCompany());
        return applicationFindDto;
    }

    @Override
    public ApplicationUpdateDto update(String name, ApplicationUpdateDto applicationUpdateDto) throws CustomException {
        Application application = parseFromEntity(findByName(name));
        String message = ChangeBeanUtils.checkApplication(application, applicationUpdateDto, companyService);
        if (!message.isEmpty()){
            repository.save(application);
            log.debug("Application Save!");
        }
        return applicationUpdateDto;
    }

    @Override
    public void deleteLogic(String name) throws CustomException {
        log.debug("Delete logic: {}", name);
        Application application = parseFromEntity(findByName(name));
        repository.deleteLogic(name, !application.getEliminate(), application.getEliminate());
    }

    @Override
    public Page<ApplicationPaggeableDto> findAll(String filter, Long companyId, Pageable pageable) {
        Page<Application> applications = findByFilter(filter, companyId, null, null, pageable);
        List<ApplicationPaggeableDto> applicationFindDto = new ArrayList<>();
        applications.forEach( application -> {
            try {
                applicationFindDto.add(parseApplicationPaggeableDto(application));
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        } );
        return new PageImpl<>(applicationFindDto, pageable, applications.getTotalElements());
    }

    private ApplicationPaggeableDto parseApplicationPaggeableDto(Application application) throws CustomException {
        ApplicationPaggeableDto applicationFindDto = from_M_To_N(application, ApplicationPaggeableDto.class);
        applicationFindDto.setName(application.getName());
        applicationFindDto.setDescription(application.getDescription());
        applicationFindDto.setCompany(application.getCompany());
        applicationFindDto.setCreatedBy(application.getCreatedBy());
        applicationFindDto.setCreationDate(application.getCreationDate());
        return applicationFindDto;
    }

    private Page<Application> findByFilter(String filter, Long companyId, Long createdBy, Boolean active,
                                        Pageable pageable) {
        return repository.findAll((Specification<Application>) (root, query, criteriaBuilder) ->
                getPredicateDinamycFilter(filter, companyId, createdBy, active, criteriaBuilder, root), pageable);
    }

    @Override
    public ApplicationFindDto findByName(String name) throws CustomException {
        return from_M_To_N(findByName(name), ApplicationFindDto.class);
    }

    private void validationSave(ApplicationDto applicationDto, Application application) throws CustomException {
        log.debug("Aqui se esta llamando el validationSave {}", applicationDto.getName());
        validateName(applicationDto.getName());
        // TODO: Agregar validaciones que se necesiten
        application.setCreatedBy(getCurrentUser().getUserId());
    }

    private void validateName(String name) throws CustomException {
        try {
            log.debug("Aqui esta en el validateRFC: {}", name);
            ApplicationFindDto applicationFindDto = findByName(name);
            log.debug("RFC {} already exist for company {} {}", name, applicationFindDto.getName(), applicationFindDto.getName());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.APPLICATION_NAME_DUPLICATED, name), null);
        } catch (CustomException e) {
            log.error("Validating RFC Exception: {}", e.getMessage());
            if ( e instanceof BadRequestException)
                throw e;
        }
    }

    private Predicate getPredicateDinamycFilter(String filter, Long companyId, Long createdBy, Boolean active,
                                                CriteriaBuilder builder, Root<Application> root) {
        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(builder.isFalse(root.get(Employee.Fields.eliminate)));
        //predicates.add(root.get(Employee.Fields.id).in(employessNotIn()).not());

        if(!StringUtils.isEmpty(filter)) {
            String patternFilter = String.format(GeneralKeys.PATTERN_LIKE, filter.toLowerCase());
            Predicate pName = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Application.Fields.name))), patternFilter);
            Predicate pDescription = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Application.Fields.description))), patternFilter);
            predicates.add(builder.or(pName, pDescription));
        }

        if(companyId != null) {
            predicates.add(builder.equal(root.get(Application.Fields.company), companyId));
        }

        if(createdBy != null) {
            predicates.add(builder.equal(root.get(Application.Fields.createdBy), createdBy));
        }

        if(active != null) {
            predicates.add(builder.equal(root.get(Application.Fields.active), active));
        }

        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

}
