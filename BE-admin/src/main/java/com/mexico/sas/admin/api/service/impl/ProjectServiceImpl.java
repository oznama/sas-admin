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
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
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
        Long roleId = getCurrentUser().getRoleId();
        Long companyId = getCurrentUser().getCompanyId();
        log.debug("Finding all projects with pagination for employee of company {}", companyId);
        Page<Project> projects = roleId.equals(CatalogKeys.ROLE_ROOT) ? repository.findAll(pageable)
                : (roleId.equals(CatalogKeys.ROLE_ADMIN)
                    ? repository.findByCompany(new Company(companyId), pageable)
                    : repository.findByCompanyAndCreatedBy(new Company(companyId), getCurrentUser().getUserId(), pageable));

//        long total = roleId.equals(CatalogKeys.ROLE_ROOT) ? repository.count()
//                : (roleId.equals(CatalogKeys.ROLE_ADMIN)
//                ? repository.countByCompany(new Company(companyId))
//                : repository.countByCompanyAndCreatedBy(new Company(companyId), getCurrentUser().getUserId()));

        List<ProjectPageableDto> projectsPageableDto = new ArrayList<>();

        projects.forEach( project -> {
            try {
                projectsPageableDto.add(parseProjectPagged(project));
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        });
        return new PageImpl<>(projectsPageableDto, pageable, projects.getTotalElements());
    }

    @Override
    public ProjectFindDto findById(Long id) throws CustomException {
        Project project = findEntityById(id);
        ProjectFindDto projectFindDto = parseFromEntity(project);
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
        return projectFindDto;
    }

    @Override
    public ProjectFindDto findByKey(String key) throws CustomException {
        return parseFromEntity(repository.findByKey(key)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_BYKEY_NOT_FOUND, key))));
    }

    @Override
    public Project findEntityById(Long id) throws CustomException {
        return repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_NOT_FOUND, id)));
    }

    @Override
    public void save(ProjectDto projectDto) throws CustomException {
        log.debug(" ::: Request to save ::: {}", projectDto);
        Project project = from_M_To_N(projectDto, Project.class);
        validationSave(projectDto, project);
        try {
            log.debug("Project to save, key: {}", project.getKey());
            repository.save(project);
            projectDto.setId(project.getId());
            log.debug("Project created with id: {}", projectDto.getId());
            save(Project.class.getSimpleName(), project.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                    I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.PROJECT_NOT_CREATED, projectDto.getKey());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(Long projectId, ProjectUpdateDto projectUpdateDto) throws CustomException {
        Project project = findEntityById(projectId);
        String message = ChangeBeanUtils.checkProyect(project, projectUpdateDto);
        if(!message.isEmpty()) {
            repository.save(project);
            save(Project.class.getSimpleName(), project.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
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
                    I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
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
        String message = ChangeBeanUtils.checkProjectApplication(projectApplication, projectApplicationUpdateDto);

        if(!message.isEmpty()) {
            projectApplicationRepository.save(projectApplication);
            save(ProjectApplication.class.getSimpleName(), projectApplication.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
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

    private ProjectFindDto parseFromEntity(Project project) throws CustomException {
        ProjectFindDto projectFindDto = from_M_To_N(project, ProjectFindDto.class);
        projectFindDto.setCreatedBy(logMovementService
                .findFirstMovement(Project.class.getSimpleName(), project.getId()).getUserName());
        projectFindDto.setCompanyId(project.getCompany().getId());
        projectFindDto.setProjectManagerId(project.getProjectManager().getId());
        projectFindDto.setCreationDate(dateToString(project.getCreationDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectFindDto.setInstallationDate(dateToString(project.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return projectFindDto;
    }

    private ProjectPageableDto parseProjectPagged(Project project) throws CustomException {
        ProjectPageableDto projectPageableDto = from_M_To_N(project, ProjectPageableDto.class);
        projectPageableDto.setCreatedBy(logMovementService
                .findFirstMovement(Project.class.getSimpleName(), project.getId()).getUserName());
        projectPageableDto.setCompany(project.getCompany().getName());
        projectPageableDto.setProjectManager(buildFullname(project.getProjectManager()));
        projectPageableDto.setCreationDate(dateToString(project.getCreationDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectPageableDto.setInstallationDate(dateToString(project.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return projectPageableDto;
    }

    private ProjectApplicationDto parseFromEntity(ProjectApplication projectApplication) throws CustomException {
        ProjectApplicationDto projectApplicationDto = from_M_To_N(projectApplication, ProjectApplicationDto.class);
        projectApplicationDto.setProjectId(projectApplication.getProject().getId());
        projectApplicationDto.setLeaderId(projectApplication.getLeader().getId());
        projectApplicationDto.setDeveloperId(projectApplication.getDeveloper().getId());
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
        projectApplicationFindDto.setDesignDate(dateToString(projectApplication.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationFindDto.setDevelopmentDate(dateToString(projectApplication.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationFindDto.setEndDate(dateToString(projectApplication.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return projectApplicationFindDto;
    }

    private void validationSave(ProjectDto projectDto, Project project) throws CustomException {
        project.setInstallationDate(stringToDate(projectDto.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY));
        try {
            findByKey(project.getKey());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.PROJECT_KEY_DUPLICATED, projectDto.getKey()), null);
        } catch (CustomException e) {
            if ( e instanceof BadRequestException)
                throw e;
        }
        Company company = new Company(getCurrentUser().getCompanyId());
        project.setCompany(company);
        employeeService.findByCompanyAndId(company.getId() , projectDto.getProjectManagerId());
        project.setProjectManager(new Employee(projectDto.getProjectManagerId()));
        project.setCreatedBy(getCurrentUser().getUserId());
    }

    private void validationSave(ProjectApplicationDto projectApplicationDto, ProjectApplication projectApplication) throws CustomException {
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
}
