package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.dto.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.exception.ValidationRequestException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Client;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.model.User;
import com.mexico.sas.admin.api.repository.ProjectRepository;
import com.mexico.sas.admin.api.service.ProjectService;
import com.mexico.sas.admin.api.service.UserService;
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
    private UserService userService;

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
            log.debug("Project to save: {}", project);
            repository.save(project);
            log.debug("Project created with id: {}", project.getId());
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        projectDto.setId(project.getId());
    }

    private void validationSave(ProjectDto projectDto, Project project) throws ValidationRequestException {
        List<ResponseErrorDetailDto> errors = new ArrayList<>();

        // Valiadcion de key
        try {
            findByKey(project.getKey());
            errors.add(new ResponseErrorDetailDto(ProjectDto.Fields.key, I18nResolver.getMessage(I18nKeys.PROJECT_KEY_DUPLICATED)));
        } catch (CustomException ignored) {

        }

        // TODO Agregar catalogo de estados
        project.setStatus(2000200002l);
        // TODO Agregar validacion para que no inserte clientes que no existen
        project.setClient(new Client(projectDto.getClientId()));
        // TODO Agregar validacion para que el pm sea empleado del cliente
        project.setProjectManager(new Employee(projectDto.getProjectManagerId()));

        project.setCreationDate(new Date());
    }
}
