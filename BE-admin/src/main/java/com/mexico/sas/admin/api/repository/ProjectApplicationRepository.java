package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.model.ProjectApplications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectApplicationsRepository extends JpaRepository<ProjectApplications, Long> {

    List<ProjectApplications> findByProject(Project project);

}
