package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Company;
import com.mexico.sas.admin.api.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByCompanyAndCreatedBy(Company company, Long createdBy, Pageable pageable);
    Optional<Project> findByIdAndActiveIsTrueAndEliminateIsFalse(Long id);
    Optional<Project> findByKey(String key);
}
