package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.project.ProjectDto;
import com.mexico.sas.admin.api.dto.project.ProjectFindDto;
import com.mexico.sas.admin.api.dto.project.ProjectPageableDto;
import com.mexico.sas.admin.api.dto.project.ProjectUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    Page<ProjectPageableDto> findAll(String filter, Pageable pageable);

    ProjectFindDto findById(Long id) throws CustomException;
    ProjectFindDto findByKey(String key) throws CustomException;
    Project findEntityById(Long id) throws CustomException;
    void save(ProjectDto projectDto) throws CustomException;
    void update(Long projectId, ProjectUpdateDto projectUpdateDto) throws CustomException;

}
