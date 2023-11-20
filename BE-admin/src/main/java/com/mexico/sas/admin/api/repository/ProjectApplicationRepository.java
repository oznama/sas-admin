package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.model.ProjectApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {
    Optional<ProjectApplication> findByProjectAndId(Project project, Long id);
    Optional<ProjectApplication> findByProjectAndApplicationId(Project project, Long applicationId);
    List<ProjectApplication> findByProject(Project project);

}
