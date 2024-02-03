package com.mexico.sas.admin.api.repository;

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
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    List<Company> findByActiveIsTrueAndEliminateIsFalse();

    Optional<Company> findByRfc(String rfc);

    @Transactional
    @Modifying
    @Query("update Company c set c.eliminate = :eliminate, c.active = :active where c.id = :id")
    void deleteLogic(@Param(value = "id") Long id, @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);
}
