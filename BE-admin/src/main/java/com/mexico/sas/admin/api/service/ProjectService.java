package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.project.ProjectDto;
import com.mexico.sas.admin.api.dto.project.ProjectFindDto;
import com.mexico.sas.admin.api.dto.project.ProjectPageableDto;
import com.mexico.sas.admin.api.dto.project.ProjectUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProjectService {
    Page<ProjectPageableDto> findAll(String filter, Pageable pageable);

    ProjectFindDto findByKey(String key) throws CustomException;
    Project findEntityByKey(String key) throws CustomException;
    void save(ProjectDto projectDto) throws CustomException;
    void update(String key, ProjectUpdateDto projectUpdateDto) throws CustomException;

    void updateAmounts(String key, BigDecimal amount, BigDecimal tax, BigDecimal total);
    void deleteLogic(String key) throws CustomException;
    void delete(String key) throws CustomException;

    List<SelectDto> getForSelect();

}
