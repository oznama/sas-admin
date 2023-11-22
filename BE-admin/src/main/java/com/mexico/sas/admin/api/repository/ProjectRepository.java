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

    Long countByCompany(Company company);
    Long countByCompanyAndCreatedBy(Company company, Long createdBy);
    Page<Project> findByCompany(Company company, Pageable pageable);
    Page<Project> findByCompanyAndCreatedBy(Company company, Long createdBy, Pageable pageable);
    Optional<Project> findByIdAndActiveIsTrueAndEliminateIsFalse(Long id);
    Optional<Project> findByKey(String key);
}
