package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.application.*;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Application;
import com.mexico.sas.admin.api.model.Order;
import com.mexico.sas.admin.api.repository.ApplicationRepository;
import com.mexico.sas.admin.api.service.ApplicationService;
import com.mexico.sas.admin.api.service.CompanyService;
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public void update(String name, ApplicationUpdateDto applicationDto) throws CustomException {
        Application application = parseFromEntity(findByName(name));
        String message = ChangeBeanUtils.checkApplication(application, applicationDto, companyService);
        if (!message.isEmpty()){
            repository.save(application);
            log.debug("Application Save!");
        }
    }

    @Override
    public void deleteLogic(String name) throws CustomException {
        log.debug("Delete logic: {}", name);
        Application application = parseFromEntity(findByName(name));
        repository.deleteLogic(name, !application.getEliminate() ? CatalogKeys.ORDER_STATUS_CANCELED : CatalogKeys.ORDER_STATUS_IN_PROCESS,
                !application.getEliminate(), application.getEliminate());
    }

    @Override
    public Page<ApplicationPaggeableDto> findAll(String filter, Long type, Pageable pageable) {
        return null;
    }

    @Override
    public ApplicationFindDto findByName(String name) throws CustomException {
        return null;
    }

    @Override
    public List<ApplicationFindSelectDto> getForSelect() {
        return null;
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
}
