package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.dto.project.ProjectApplicationDto;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.model.ProjectApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {
    List<ProjectApplication> findByProject(Project project);

}
