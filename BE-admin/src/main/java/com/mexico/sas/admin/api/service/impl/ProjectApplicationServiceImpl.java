package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.project.*;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.model.ProjectApplication;
import com.mexico.sas.admin.api.repository.ProjectApplicationRepository;
import com.mexico.sas.admin.api.service.*;
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProjectApplicationServiceImpl extends LogMovementUtils implements ProjectApplicationService {

    @Autowired
    private ProjectApplicationRepository repository;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LogMovementService logMovementService;

    @Autowired
    private ProjectService projectService;

    @Override
    public void save(ProjectApplicationDto projectApplicationDto) throws CustomException {
        ProjectApplication projectApplication = from_M_To_N(projectApplicationDto, ProjectApplication.class);
        validationSave(projectApplicationDto, projectApplication);
        try {
            log.debug("Application {} for project id {} to save",
                    projectApplicationDto.getApplicationId(), projectApplicationDto.getProjectId());
            log.debug("Entity ::: {}", projectApplication);
            repository.save(projectApplication);
            save(ProjectApplication.class.getSimpleName(), projectApplication.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                    I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
            projectApplicationDto.setId(projectApplication.getId());
            log.debug("Application for project created with id: {}", projectApplicationDto.getId());
            updateProjectAmount(projectApplication.getProject());
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_CREATED,
                    projectApplicationDto.getApplicationId(), projectApplicationDto.getProjectId());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(Long projectApplicationId, ProjectApplicationUpdateDto projectApplicationUpdateDto) throws CustomException {
        ProjectApplication projectApplication = findEntityByApplicationId(projectApplicationId);
        String message = ChangeBeanUtils.checkProjectApplication(projectApplication, projectApplicationUpdateDto);

        if(!message.isEmpty()) {
            repository.save(projectApplication);
            save(ProjectApplication.class.getSimpleName(), projectApplication.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
            log.debug("Application updated!");
            updateProjectAmount(projectApplication.getProject());
        }
    }

    @Override
    public ProjectApplicationFindDto findByApplicationId(Long id) throws CustomException {
        return getProjectApplicationFindDto(findEntityByApplicationId(id));
    }

    @Override
    public ProjectApplication findEntityByApplicationId(Long id) throws CustomException {
        log.debug("Finding project application: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, id)));
    }

    @Override
    public List<ProjectApplicationFindDto> findByProjectId(Long projectId) {
        List<ProjectApplication> projectApplications = repository.findByProject(new Project(projectId));
        List<ProjectApplicationFindDto> applications = new ArrayList<>();
        projectApplications.forEach( pa -> {
            try {
                applications.add(getProjectApplicationFindDto(pa));
            } catch (CustomException e) {
                log.error("Impossible add project application {}, error: {}", pa.getId(), e.getMessage());
            }
        });
        try {
            applications.add(getTotal(projectApplications));
        } catch (CustomException e) {
            log.error("Impossible add project application total, error: {}", e.getMessage());
        }
        return applications;
    }

    @Override
    public ProjectApplicationDto findByProjectAndId(Long projectId, Long id) throws CustomException {
        log.debug("Finding project application with projectId: {} and id: {}", projectId, id);
        return parseFromEntity(repository.findByProjectAndId(new Project(projectId), id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, id))));
    }

    @Override
    public ProjectApplicationDto findByProjectAndApplicationId(Long projectId, Long applicationId) throws CustomException {
        log.debug("Finding project application with projectId: {} and applicationId: {}", projectId, applicationId);
        return parseFromEntity(repository.findByProjectAndApplicationIdAndActiveIsTrueAndEliminateIsFalse(new Project(projectId), applicationId)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, applicationId))));
    }

    private ProjectApplicationDto parseFromEntity(ProjectApplication projectApplication) throws CustomException {
        ProjectApplicationDto projectApplicationDto = from_M_To_N(projectApplication, ProjectApplicationDto.class);
        projectApplicationDto.setProjectId(projectApplication.getProject().getId());
        projectApplicationDto.setLeaderId(projectApplication.getLeader().getId());
        projectApplicationDto.setDeveloperId(projectApplication.getDeveloper().getId());
        projectApplicationDto.setStartDate(dateToString(projectApplication.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationDto.setDesignDate(dateToString(projectApplication.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationDto.setDevelopmentDate(dateToString(projectApplication.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationDto.setEndDate(dateToString(projectApplication.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return projectApplicationDto;
    }

    private ProjectApplicationFindDto getProjectApplicationFindDto(ProjectApplication projectApplication) throws CustomException {
        ProjectApplicationFindDto projectApplicationFindDto = from_M_To_N(projectApplication, ProjectApplicationFindDto.class);
        projectApplicationFindDto.setApplication(catalogService.findById(projectApplication.getApplicationId()).getValue());
        projectApplicationFindDto.setLeader(buildFullname(projectApplication.getLeader()));
        projectApplicationFindDto.setDeveloper(buildFullname(projectApplication.getDeveloper()));
        projectApplicationFindDto.setAmount(formatCurrency(projectApplication.getAmount().doubleValue()));
        projectApplicationFindDto.setTax(formatCurrency(projectApplication.getTax().doubleValue()));
        projectApplicationFindDto.setTotal(formatCurrency(projectApplication.getTotal().doubleValue()));
        projectApplicationFindDto.setStartDate(dateToString(projectApplication.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationFindDto.setDesignDate(dateToString(projectApplication.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationFindDto.setDevelopmentDate(dateToString(projectApplication.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationFindDto.setEndDate(dateToString(projectApplication.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return projectApplicationFindDto;
    }

    private ProjectApplicationFindDto getTotal(List<ProjectApplication> projectApplications) throws CustomException {
        BigDecimal totalAmount = projectApplications.stream().map( pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = projectApplications.stream().map( pa -> pa.getTax() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalT = projectApplications.stream().map( pa -> pa.getTotal() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        ProjectApplicationFindDto projectApplicationFindDto = new ProjectApplicationFindDto();
        projectApplicationFindDto.setApplication(GeneralKeys.FOOTER_TOTAL);
        projectApplicationFindDto.setAmount(formatCurrency(totalAmount.doubleValue()));
        projectApplicationFindDto.setTax(formatCurrency(totalTax.doubleValue()));
        projectApplicationFindDto.setTotal(formatCurrency(totalT.doubleValue()));
        return projectApplicationFindDto;
    }

    private void validationSave(ProjectApplicationDto projectApplicationDto, ProjectApplication projectApplication) throws CustomException {
        projectApplication.setStartDate(stringToDate(projectApplicationDto.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY));
        projectApplication.setDesignDate(stringToDate(projectApplicationDto.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY));
        projectApplication.setDevelopmentDate(stringToDate(projectApplicationDto.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY));
        projectApplication.setEndDate(stringToDate(projectApplicationDto.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY));
        try {
            findByProjectAndApplicationId(projectApplicationDto.getProjectId(), projectApplicationDto.getApplicationId());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_DUPLICATED,
                    catalogService.findById(projectApplication.getApplicationId()).getValue(),
                    projectApplicationDto.getProjectId()), null);
        } catch (CustomException e) {
            if(e instanceof BadRequestException)
                throw e;
        }

        projectApplication.setProject(new Project(projectApplicationDto.getProjectId()));
        projectApplication.setLeader(employeeService.findEntityById(projectApplicationDto.getLeaderId()));
        projectApplication.setDeveloper(employeeService.findEntityById(projectApplicationDto.getDeveloperId()));
        projectApplication.setCreatedBy(getCurrentUser().getUserId());
    }

    private void updateProjectAmount(Project project) {
        List<ProjectApplication> projectApplications = repository.findByProject(project);
        BigDecimal amount = projectApplications.stream()
                .map( pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = projectApplications.stream()
                .map( pa -> pa.getTax() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = projectApplications.stream()
                .map( pa -> pa.getTotal() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.debug("Update project totals ...");
        projectService.updateAmounts(project.getId(), amount, tax, total);
    }
}
