package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Company;
import com.mexico.sas.admin.api.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String>, JpaSpecificationExecutor<Project> {

    List<Project> findByActiveIsTrueAndEliminateIsFalseOrderByKeyAscDescriptionAsc();

    //    Long countByCompany(Company company);
//    Long countByCompanyAndCreatedBy(Company company, Long createdBy);
//    Page<Project> findByCompany(Company company, Pageable pageable);
//    Page<Project> findByCompanyAndCreatedBy(Company company, Long createdBy, Pageable pageable);
    Optional<Project> findByKeyAndActiveIsTrueAndEliminateIsFalse(String projectKey);

    @Transactional
    @Modifying
    @Query("update Project p set p.amount = :amount, p.tax = :tax, p.total = :total where p.key = :key")
    void updateAmount(@Param(value = "key") String key,
                      @Param(value = "amount") BigDecimal amount,
                      @Param(value = "tax") BigDecimal tax,
                      @Param(value = "total") BigDecimal total);

    @Transactional
    @Modifying
    @Query("update Project p set p.eliminate = :eliminate, p.active = :active where p.key = :key")
    void deleteLogic(@Param(value = "key") String key, @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);
}
