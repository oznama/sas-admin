package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.project.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.model.ProjectApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    Page<ProjectPageableDto> findAll(Pageable pageable);

    ProjectFindDto findById(Long id) throws CustomException;
    ProjectFindDto findByKey(String key) throws CustomException;
    Project findEntityById(Long id) throws CustomException;
    void save(ProjectDto projectDto) throws CustomException;
    void update(Long projectId, ProjectUpdateDto projectUpdateDto) throws CustomException;

    void save(ProjectApplicationDto projectApplicationDto) throws CustomException;
    void update(Long projectApplicationId, ProjectApplicationUpdateDto projectApplicationUpdateDto) throws CustomException;
    ProjectApplicationFindDto findByApplicationId(Long id) throws CustomException;
    ProjectApplication findEntityByApplicationId(Long id) throws CustomException;
    ProjectApplicationDto findByProjectAndId(Long projectId, Long id) throws CustomException;
    ProjectApplicationDto findByProjectAndApplicationId(Long projectId, Long applicationId) throws CustomException;

}
