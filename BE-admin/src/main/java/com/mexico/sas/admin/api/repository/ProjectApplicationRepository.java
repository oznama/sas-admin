package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Application;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.model.ProjectApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long>, JpaSpecificationExecutor<ProjectApplication> {
    Optional<ProjectApplication> findByProjectAndId(Project project, Long id);
    Optional<ProjectApplication> findByProjectAndApplicationAndActiveIsTrueAndEliminateIsFalse(Project project, Application application);
    List<ProjectApplication> findByProjectOrderByIdAsc(Project project);

    List<ProjectApplication> findByProjectAndActiveIsTrueAndEliminateIsFalse(Project project);

    @Transactional
    @Modifying
    @Query("update ProjectApplication p set p.eliminate = :eliminate, p.active = :active where p.id = :id")
    void deleteLogic(@Param(value = "id") Long id, @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);
}
