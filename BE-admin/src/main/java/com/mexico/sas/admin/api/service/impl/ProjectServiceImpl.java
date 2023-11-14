package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.project.*;
import com.mexico.sas.admin.api.dto.user.UserFindDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.*;
import com.mexico.sas.admin.api.repository.ProjectApplicationRepository;
import com.mexico.sas.admin.api.repository.ProjectRepository;
import com.mexico.sas.admin.api.service.*;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProjectServiceImpl extends Utils implements ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectApplicationRepository projectApplicationRepository;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Page<ProjectPageableDto> findAll(Pageable pageable) {

        Page<Project> projects = repository.findAll(pageable);
        List<ProjectPageableDto> projectsPageableDto = new ArrayList<>();

        projects.forEach( project -> {
            try {
                ProjectPageableDto projectPageableDto = from_M_To_N(project, ProjectPageableDto.class);
                UserFindDto userCreater = userService.findById(project.getUserId());
                projectPageableDto.setCreatedBy(buildFullname(userCreater.getName(),
                        null,
                        userCreater.getSurname(),
                        userCreater.getSecondSurname()));
                projectPageableDto.setClient(project.getClient().getName());
                projectPageableDto.setProjectManager(buildFullname(
                        project.getProjectManager().getName(),
                        project.getProjectManager().getSecondName(),
                        project.getProjectManager().getSurname(),
                        project.getProjectManager().getSecondSurname()));
                projectsPageableDto.add(projectPageableDto);
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        });

        return new PageImpl<>(projectsPageableDto, pageable, repository.count());
    }

    @Override
    public ProjectFindDto findById(Long id) throws CustomException {
        Project project = repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_NOT_FOUND, id)));
        ProjectFindDto projectFindDto = from_M_To_N(project, ProjectFindDto.class);
        UserFindDto creater = userService.findById(project.getUserId());
        projectFindDto.setCreatedBy(buildFullname(creater.getName(), null, creater.getSurname(), creater.getSecondSurname()));
        projectFindDto.setClientId(project.getClient().getId());
        projectFindDto.setProjectManagerId(project.getProjectManager().getId());
        List<ProjectApplication> projectApplications = projectApplicationRepository.findByProject(project);
        List<ProjectApplicationFindDto> applications = new ArrayList<>();
        projectApplications.forEach( pa -> {
            try {
                ProjectApplicationFindDto projectApplicationFindDto = from_M_To_N(pa, ProjectApplicationFindDto.class);
                projectApplicationFindDto.setApplication(catalogService.findById(pa.getApplicationId()).getValue());
                projectApplicationFindDto.setLeader(buildFullname( pa.getLeader().getName(), null, pa.getLeader().getSurname(), pa.getLeader().getSecondSurname()));
                projectApplicationFindDto.setDeveloper(buildFullname( pa.getDeveloper().getName(), null, pa.getDeveloper().getSurname(), pa.getDeveloper().getSecondSurname()));
                projectApplicationFindDto.setAmount(formatCurrency(pa.getAmount().doubleValue()));
                applications.add(projectApplicationFindDto);
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        });
        projectFindDto.setApplications(applications);
        return projectFindDto;
    }

    @Override
    public ProjectFindDto findByKey(String key) throws CustomException {
        Project project = repository.findByKey(key)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_BYKEY_NOT_FOUND, key)));
        return from_M_To_N(project, ProjectFindDto.class);
    }

    @Override
    public void save(ProjectDto projectDto) throws CustomException {
        Project project = from_M_To_N(projectDto, Project.class);
        project.setUserId(getCurrentUserId());
        validationSave(projectDto, project);
        try {
            log.debug("Project to save, key: {}", project.getKey());
            repository.save(project);
            projectDto.setId(project.getId());
            log.debug("Project created with id: {}", projectDto.getId());
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.PROJECT_NOT_CREATED, projectDto.getKey());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(Long projectId, ProjectUpdateDto projectUpdateDto) throws CustomException {
        Project project = repository.findById(projectId).orElseThrow( () ->
                new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_NOT_FOUND, projectId)));
        if( !project.getDescription().equals(projectUpdateDto.getDescription()) ) {
            project.setDescription(projectUpdateDto.getDescription());
        }
        if( !project.getClient().getId().equals(projectUpdateDto.getClientId()) ) {
            project.setClient(new Client(projectUpdateDto.getClientId()));
        }
        if( !project.getProjectManager().getId().equals(projectUpdateDto.getProjectManagerId()) ) {
            project.setProjectManager(new Employee(projectUpdateDto.getProjectManagerId()));
        }
        if( !project.getInstallationDate().equals(projectUpdateDto.getInstallationDate()) ) {
            project.setInstallationDate(projectUpdateDto.getInstallationDate());
        }
        repository.save(project);
    }

    @Override
    public void save(ProjectApplicationDto projectApplicationDto) throws CustomException {
        log.debug("DTO ::: {}", projectApplicationDto);
        ProjectApplication projectApplication = from_M_To_N(projectApplicationDto, ProjectApplication.class);
        projectApplication.setUserId(getCurrentUserId());
        validationSave(projectApplicationDto, projectApplication);
        try {
            log.debug("Application {} for project id {} to save",
                    projectApplicationDto.getApplicationId(), projectApplicationDto.getProjectId());
            log.debug("Entity ::: {}", projectApplication);
            projectApplicationRepository.save(projectApplication);
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
    public ProjectApplicationDto findById(Long projectId, Long applicationId) throws CustomException {
        findById(projectId);
        ProjectApplication projectApplication = projectApplicationRepository.findById(applicationId).orElseThrow(() ->
                new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, applicationId)));
        ProjectApplicationDto projectApplicationDto = from_M_To_N(projectApplication, ProjectApplicationDto.class);
        projectApplicationDto.setProjectId(projectApplication.getProject().getId());
        projectApplicationDto.setLeaderId(projectApplication.getLeader().getId());
        projectApplicationDto.setDeveloperId(projectApplication.getDeveloper().getId());
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

        clientService.findById(projectDto.getClientId());
        project.setClient(new Client(projectDto.getClientId()));

        employeeService.findByClientAndId(projectDto.getClientId() , projectDto.getProjectManagerId());
        project.setProjectManager(new Employee(projectDto.getProjectManagerId()));

        project.setStatus(CatalogKeys.ESTATUS_MACHINE_ENABLED);

        if(project.getCreationDate() == null)
            project.setCreationDate(new Date());
    }

    private void validationSave(ProjectApplicationDto projectApplicationDto, ProjectApplication projectApplication) throws CustomException {
        // TODO
        projectApplication.setProject(new Project(projectApplicationDto.getProjectId()));
        projectApplication.setLeader(new User(projectApplicationDto.getLeaderId()));
        projectApplication.setDeveloper(new User(projectApplicationDto.getDeveloperId()));

        if(projectApplication.getCreationDate() == null)
            projectApplication.setCreationDate(new Date());
    }
}
