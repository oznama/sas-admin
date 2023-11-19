package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByActiveIsTrueAndEliminateIsFalse();
}
