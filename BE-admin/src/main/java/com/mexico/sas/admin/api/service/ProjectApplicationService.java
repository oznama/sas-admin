package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.project.ProjectApplicationDto;
import com.mexico.sas.admin.api.dto.project.ProjectApplicationFindDto;
import com.mexico.sas.admin.api.dto.project.ProjectApplicationPaggeableDto;
import com.mexico.sas.admin.api.dto.project.ProjectApplicationUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.ProjectApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectApplicationService {

    void save(ProjectApplicationDto projectApplicationDto) throws CustomException;
    void update(Long projectApplicationId, ProjectApplicationUpdateDto projectApplicationUpdateDto) throws CustomException;
    void deleteLogic(Long id) throws CustomException;
    void delete(Long id) throws CustomException;
    ProjectApplicationFindDto findByApplicationId(Long id) throws CustomException;
    ProjectApplication findEntityByApplicationId(Long id) throws CustomException;
    List<ProjectApplicationPaggeableDto> findByProjectKey(String projectKey) throws CustomException;
    ProjectApplicationDto findByProjectKeyAndId(String projectKey, Long id) throws CustomException;
    ProjectApplicationDto findByProjectAndApplication(String projectKey, String application) throws CustomException;

    Page<ProjectApplicationPaggeableDto> findPendingsByEmployee(String filter, Pageable pageable) throws CustomException;

}
