package com.mexico.sas.admin.api.repository;

import java.util.List;
import java.util.Optional;

import com.mexico.sas.admin.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> , JpaSpecificationExecutor<User> {
  Optional<User> findByIdAndEliminateFalse(Long id);
  Optional<User> findByEmployeeId(Long employeeId);
  Optional<User> findByEmployeeIdAndPasswordAndEliminateFalse(Long employeId, String password);
  List<User> findByRoleIdAndActiveIsTrueAndEliminateFalse(Long roleId);
  Page<User> findByEliminateFalse(Pageable pageable);
  Page<User> findByActiveAndEliminateFalse(Boolean active, Pageable pageable);
  Long countByActiveAndEliminateFalse(Boolean active);
  Long countByEliminateFalse();
  @Transactional
  @Modifying
  @Query("update User u set u.active = :active where u.id = :id")
  void setActive(@Param(value = "id") Long id, @Param(value = "active") Boolean active);
  @Transactional
  @Modifying
  @Query("update User u set u.eliminate = :eliminate, u.active = :active where u.id = :id")
  void deleteLogic(@Param(value = "id") Long id, @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);

}
