package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmailIgnoreCaseAndActiveIsTrueAndEliminateIsFalse(String email);
    Optional<Employee> findByCompanyIdAndIdAndActiveIsTrueAndEliminateIsFalse(Long companyId, Long id);
    List<Employee> findByPositionIdNotInAndActiveIsTrueAndEliminateIsFalse(List<Long> positionIds);
    List<Employee> findByCompanyIdAndPositionIdNotInAndActiveIsTrueAndEliminateIsFalse(Long companyId, List<Long> positionIds);
    List<Employee> findByCompanyIdAndPositionIdAndActiveIsTrueAndEliminateIsFalse(Long companyId, Long positionId);
}
