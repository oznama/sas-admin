package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Page<Employee> findByIdNotIn(List<Long> ids, Pageable pageable);

    Optional<Employee> findByEmailIgnoreCaseAndActiveIsTrueAndEliminateIsFalse(String email);
    Optional<Employee> findByCompanyIdAndIdAndActiveIsTrueAndEliminateIsFalse(Long companyId, Long id);
    List<Employee> findByPositionIdNotInAndActiveIsTrueAndEliminateIsFalse(List<Long> positionIds);
    List<Employee> findByCompanyIdAndPositionIdNotInAndActiveIsTrueAndEliminateIsFalse(Long companyId, List<Long> positionIds);
    List<Employee> findByCompanyIdAndPositionIdAndActiveIsTrueAndEliminateIsFalse(Long companyId, Long positionId);
    List<Employee> findByCompanyIdAndIdNotIn(Long companyId, List<Long> ids);

    @Transactional
    @Modifying
    @Query("update Employee e set e.eliminate = :eliminate, e.active = :active where e.id = :id")
    void deleteLogic(@Param(value = "id") Long id, @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);
}
