package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.exception.ValidationRequestException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.*;
import com.mexico.sas.admin.api.repository.ProjectApplicationsRepository;
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
    private ProjectApplicationsRepository projectApplicationsRepository;

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
        List<ProjectApplications> projectApplications = projectApplicationsRepository.findByProject(project);
        List<ApplicationFindDto> applications = new ArrayList<>();
        projectApplications.forEach( pa -> {
            try {
                ApplicationFindDto applicationFindDto = from_M_To_N(pa, ApplicationFindDto.class);
                applicationFindDto.setApplication(catalogService.findById(pa.getApplicationId()).getValue());
                applicationFindDto.setLeader(buildFullname( pa.getLeader().getName(), null, pa.getLeader().getSurname(), pa.getLeader().getSecondSurname()));
                applicationFindDto.setDeveloper(buildFullname( pa.getDeveloper().getName(), null, pa.getDeveloper().getSurname(), pa.getDeveloper().getSecondSurname()));
                applications.add(applicationFindDto);
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

    private void validationSave(ProjectDto projectDto, Project project) throws ValidationRequestException {
        List<ResponseErrorDetailDto> errors = new ArrayList<>();

        // Valiadcion de key
        try {
            findByKey(project.getKey());
            errors.add(new ResponseErrorDetailDto(ProjectDto.Fields.key, I18nResolver.getMessage(I18nKeys.PROJECT_KEY_DUPLICATED, project.getKey())));
        } catch (CustomException ignored) {

        }

        // Validacion clientId
        try {
            clientService.findById(projectDto.getClientId());
            project.setClient(new Client(projectDto.getClientId()));

            // Validacion Project Manager
            try {
                employeeService.findByClientAndId(projectDto.getClientId() , projectDto.getProjectManagerId());
                project.setProjectManager(new Employee(projectDto.getProjectManagerId()));
            } catch (CustomException ne) {
                errors.add(new ResponseErrorDetailDto(ProjectDto.Fields.projectManagerId, ne.getMessage()));
            }

        } catch (CustomException e) {
            errors.add(new ResponseErrorDetailDto(ProjectDto.Fields.clientId, e.getMessage()));
        }

        if(!errors.isEmpty()) {
            throw new ValidationRequestException(errors);
        }

        project.setStatus(CatalogKeys.ESTATUS_MACHINE_ENABLED);

        if(project.getCreationDate() == null)
            project.setCreationDate(new Date());
    }
}
