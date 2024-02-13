package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Application;
import com.mexico.sas.admin.api.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {
    /*Optional<Catalog> findByName(String id);
    @Transactional
    @Modifying
    @Query("update Applications c set c.eliminate = :eliminate, c.active = :active where c.id = :id")
    void deleteLogic(String name, @Param(value = "id") Long id, @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);*/

}
