package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Application;
import com.mexico.sas.admin.api.model.Catalog;
import com.mexico.sas.admin.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {
    List<Application> findByOrderByNameAsc();
    List<Application> findByCompanyOrderByNameAsc(Company company);
    Optional<Application> findByName(String name);
    @Transactional
    @Modifying
    @Query("update Application c set c.eliminate = :eliminate, c.active = :active where c.name = :name")
    void deleteLogic(@Param(value = "name") String name, @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);

}
