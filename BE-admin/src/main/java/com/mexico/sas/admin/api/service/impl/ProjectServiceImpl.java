package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.project.*;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.*;
import com.mexico.sas.admin.api.repository.ProjectApplicationRepository;
import com.mexico.sas.admin.api.repository.ProjectRepository;
import com.mexico.sas.admin.api.service.*;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProjectServiceImpl extends LogMovementUtils implements ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectApplicationRepository projectApplicationRepository;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LogMovementService logMovementService;

    @Override
    public Page<ProjectPageableDto> findAll(Pageable pageable) {
        Long companyId = getCurrentUser().getCompanyId();
        log.debug("Finding all projects with pagination for employee of company {}", companyId);
        Page<Project> projects = companyId.equals(CatalogKeys.COMPANY_SAS) ? repository.findAll(pageable)
                : repository.findByCompanyAndCreatedBy(new Company(companyId), getCurrentUserId(), pageable);
        List<ProjectPageableDto> projectsPageableDto = new ArrayList<>();

        projects.forEach( project -> {
            try {
                ProjectPageableDto projectPageableDto = from_M_To_N(project, ProjectPageableDto.class);
                projectPageableDto.setCreatedBy(logMovementService
                        .findFirstMovement(Project.class.getSimpleName(), project.getId()).getUserName());
                projectPageableDto.setCompany(project.getCompany().getName());
                projectPageableDto.setProjectManager(buildFullname(project.getProjectManager()));
                projectsPageableDto.add(projectPageableDto);
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        });
        return new PageImpl<>(projectsPageableDto, pageable, repository.count());
    }

    @Override
    public ProjectFindDto findById(Long id) throws CustomException {
        Project project = findEntityById(id);
        ProjectFindDto projectFindDto = from_M_To_N(project, ProjectFindDto.class);
        projectFindDto.setCreatedBy(logMovementService
                .findFirstMovement(Project.class.getSimpleName(), project.getId()).getUserName());
        projectFindDto.setCompanyId(project.getCompany().getId());
        projectFindDto.setProjectManagerId(project.getProjectManager().getId());
        List<ProjectApplication> projectApplications = projectApplicationRepository.findByProject(project);
        List<ProjectApplicationFindDto> applications = new ArrayList<>();
        projectApplications.forEach( pa -> {
            try {
                applications.add(getProjectApplicationFindDto(pa));
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        });
        projectFindDto.setApplications(applications);
        projectFindDto.setHistory(logMovementService.findByTableAndRecordId(Project.class.getSimpleName(), id));
        return projectFindDto;
    }

    @Override
    public ProjectFindDto findByKey(String key) throws CustomException {
        Project project = repository.findByKey(key)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_BYKEY_NOT_FOUND, key)));
        return from_M_To_N(project, ProjectFindDto.class);
    }

    @Override
    public Project findEntityById(Long id) throws CustomException {
        return repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_NOT_FOUND, id)));
    }

    @Override
    public void save(ProjectDto projectDto) throws CustomException {
        Project project = from_M_To_N(projectDto, Project.class);
        validationSave(projectDto, project);
        try {
            log.debug("Project to save, key: {}", project.getKey());
            repository.save(project);
            projectDto.setId(project.getId());
            log.debug("Project created with id: {}", projectDto.getId());
            save(Project.class.getSimpleName(), project.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                    I18nResolver.getMessage(I18nKeys.PROJECT_LOG_CREATED));
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.PROJECT_NOT_CREATED, projectDto.getKey());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(Long projectId, ProjectUpdateDto projectUpdateDto) throws CustomException {
        Project project = findEntityById(projectId);
        StringBuilder sb = new StringBuilder();
        if( !project.getDescription().equals(projectUpdateDto.getDescription()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.description,
                    project.getDescription(), projectUpdateDto.getDescription())).append(GeneralKeys.JUMP_LINE);
            project.setDescription(projectUpdateDto.getDescription());
        }
        if( projectUpdateDto.getCompanyId() != null && !project.getCompany().getId().equals(projectUpdateDto.getCompanyId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.companyId,
                    project.getCompany().getId(), projectUpdateDto.getCompanyId())).append(GeneralKeys.JUMP_LINE);
            project.setCompany(new Company(projectUpdateDto.getCompanyId()));
        }
        if( !project.getProjectManager().getId().equals(projectUpdateDto.getProjectManagerId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.projectManagerId,
                    project.getProjectManager().getId(), projectUpdateDto.getProjectManagerId())).append(GeneralKeys.JUMP_LINE);
            project.setProjectManager(new Employee(projectUpdateDto.getProjectManagerId()));
        }
        if( (project.getInstallationDate() == null && projectUpdateDto.getInstallationDate() != null) ||
                (!project.getInstallationDate().equals(projectUpdateDto.getInstallationDate())) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.installationDate,
                    project.getInstallationDate(), projectUpdateDto.getInstallationDate())).append(GeneralKeys.JUMP_LINE);
            project.setInstallationDate(projectUpdateDto.getInstallationDate());
        }

        if(!sb.toString().isEmpty()) {
            repository.save(project);
            save(Project.class.getSimpleName(), project.getId(), CatalogKeys.LOG_DETAIL_UPDATE, sb.toString().trim());
        }
    }

    @Override
    public void save(ProjectApplicationDto projectApplicationDto) throws CustomException {
        ProjectApplication projectApplication = from_M_To_N(projectApplicationDto, ProjectApplication.class);
        validationSave(projectApplicationDto, projectApplication);
        try {
            log.debug("Application {} for project id {} to save",
                    projectApplicationDto.getApplicationId(), projectApplicationDto.getProjectId());
            log.debug("Entity ::: {}", projectApplication);
            projectApplicationRepository.save(projectApplication);
            save(ProjectApplication.class.getSimpleName(), projectApplication.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                    I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_LOG_CREATED));
            projectApplicationDto.setId(projectApplication.getId());
            log.debug("Application for project created with id: {}", projectApplicationDto.getId());
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
        StringBuilder sb = new StringBuilder();
        if( !projectApplication.getApplicationId().equals(projectApplicationUpdateDto.getApplicationId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.applicationId,
                    projectApplication.getApplicationId(), projectApplicationUpdateDto.getApplicationId())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setApplicationId(projectApplicationUpdateDto.getApplicationId());
        }
        if( !projectApplication.getAmount().equals(projectApplicationUpdateDto.getAmount()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.amount,
                    projectApplication.getAmount(), projectApplicationUpdateDto.getAmount())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setAmount(projectApplicationUpdateDto.getAmount());
        }
        if( !projectApplication.getLeader().getId().equals(projectApplicationUpdateDto.getLeaderId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.leaderId,
                    projectApplication.getLeader().getId(), projectApplicationUpdateDto.getLeaderId())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setLeader(new Employee(projectApplicationUpdateDto.getLeaderId()));
        }
        if( !projectApplication.getDeveloper().getId().equals(projectApplicationUpdateDto.getDeveloperId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.developerId,
                    projectApplication.getDeveloper().getId(), projectApplicationUpdateDto.getDeveloperId())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setDeveloper(new Employee(projectApplicationUpdateDto.getDeveloperId()));
        }
        if( !projectApplication.getDesignDate().equals(projectApplicationUpdateDto.getDesignDate()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.designDate,
                    projectApplication.getDesignDate(), projectApplicationUpdateDto.getDesignDate())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setDesignDate(projectApplicationUpdateDto.getDesignDate());
        }
        if( !projectApplication.getDevelopmentDate().equals(projectApplicationUpdateDto.getDevelopmentDate()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.developmentDate,
                    projectApplication.getDevelopmentDate(), projectApplicationUpdateDto.getDevelopmentDate())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setDevelopmentDate(projectApplicationUpdateDto.getDevelopmentDate());
        }
        if( !projectApplication.getEndDate().equals(projectApplicationUpdateDto.getEndDate()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.endDate,
                    projectApplication.getEndDate(), projectApplicationUpdateDto.getEndDate())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setEndDate(projectApplicationUpdateDto.getEndDate());
        }

        if(!sb.toString().isEmpty()) {
            projectApplicationRepository.save(projectApplication);
            save(ProjectApplication.class.getSimpleName(), projectApplication.getId(), CatalogKeys.LOG_DETAIL_UPDATE, sb.toString().trim());
        }
    }

    @Override
    public ProjectApplicationFindDto findByApplicationId(Long id) throws CustomException {
        return getProjectApplicationFindDto(findEntityByApplicationId(id));
    }

    @Override
    public ProjectApplication findEntityByApplicationId(Long id) throws CustomException {
        log.debug("Finding project application: {}", id);
        return projectApplicationRepository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, id)));
    }

    @Override
    public ProjectApplicationDto findByProjectAndId(Long projectId, Long id) throws CustomException {
        log.debug("Finding project application with projectId: {} and id: {}", projectId, id);
        return parseFromEntity(projectApplicationRepository.findByProjectAndId(new Project(projectId), id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, id))));
    }

    @Override
    public ProjectApplicationDto findByProjectAndApplicationId(Long projectId, Long applicationId) throws CustomException {
        log.debug("Finding project application with projectId: {} and applicationId: {}", projectId, applicationId);
        return parseFromEntity(projectApplicationRepository.findByProjectAndApplicationId(new Project(projectId), applicationId)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, applicationId))));
    }

    private ProjectApplicationDto parseFromEntity(ProjectApplication projectApplication) throws CustomException {
        ProjectApplicationDto projectApplicationDto = from_M_To_N(projectApplication, ProjectApplicationDto.class);
        projectApplicationDto.setProjectId(projectApplication.getProject().getId());
        projectApplicationDto.setLeaderId(projectApplication.getLeader().getId());
        projectApplicationDto.setDeveloperId(projectApplication.getDeveloper().getId());
        projectApplicationDto.setHistory(logMovementService
                .findByTableAndRecordId(ProjectApplication.class.getSimpleName(), projectApplication.getId()));
        return projectApplicationDto;
    }

    private void validationSave(ProjectDto projectDto, Project project) throws CustomException {

        try {
            findByKey(project.getKey());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.PROJECT_KEY_DUPLICATED, projectDto.getKey()), null);
        } catch (CustomException e) {
            if ( e instanceof BadRequestException)
                throw e;
        }

        Long companyId = getCurrentUser().getCompanyId();
        Company company = null;
        if(companyId.equals(CatalogKeys.COMPANY_SAS)) {
            companyService.findById(projectDto.getCompanyId());
            company = new Company(projectDto.getCompanyId());
            project.setCompany(company);
        } else {
            company = new Company(companyId);
            project.setCompany(company);
        }

        employeeService.findByCompanyAndId(company.getId() , projectDto.getProjectManagerId());
        project.setProjectManager(new Employee(projectDto.getProjectManagerId()));

        project.setStatus(CatalogKeys.ESTATUS_MACHINE_ENABLED);
        project.setCreatedBy(getCurrentUserId());
    }

    private void validationSave(ProjectApplicationDto projectApplicationDto, ProjectApplication projectApplication) throws CustomException {

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
        projectApplication.setCreatedBy(getCurrentUserId());
    }

    private ProjectApplicationFindDto getProjectApplicationFindDto(ProjectApplication projectApplication) throws CustomException {
        ProjectApplicationFindDto projectApplicationFindDto = from_M_To_N(projectApplication, ProjectApplicationFindDto.class);
        projectApplicationFindDto.setApplication(catalogService.findById(projectApplication.getApplicationId()).getValue());
        projectApplicationFindDto.setLeader(buildFullname(projectApplication.getLeader()));
        projectApplicationFindDto.setDeveloper(buildFullname(projectApplication.getDeveloper()));
        projectApplicationFindDto.setAmount(formatCurrency(projectApplication.getAmount().doubleValue()));
        return projectApplicationFindDto;
    }
}
