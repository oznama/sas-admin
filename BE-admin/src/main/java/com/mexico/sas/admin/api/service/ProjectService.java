package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.ProjectDto;
import com.mexico.sas.admin.api.dto.ProjectFindDto;
import com.mexico.sas.admin.api.dto.ProjectPageableDto;
import com.mexico.sas.admin.api.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    Page<ProjectPageableDto> findAll(Pageable pageable);
    ProjectFindDto findByKey(String key) throws CustomException;
    void save(ProjectDto projectDto) throws CustomException;

}
