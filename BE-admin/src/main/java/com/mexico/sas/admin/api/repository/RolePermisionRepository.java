package com.mexico.sas.admin.api.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.Role;
import com.mexico.sas.admin.api.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RolePermisionRepository extends JpaRepository<RolePermission, Long> {
  List<RolePermission> findByRole(Role role);

  /*@Transactional
  @Modifying
  @Query("update RolePermission rp set rp.active = :active where rp.id = :id")
  void setActive(@Param(value = "id") Long id, @Param(value = "active") Boolean active);*/

  @Transactional
  @Modifying
  @Query("update RolePermission e set e.eliminate = :eliminate, e.active = :active where e.id = :id")
  void deleteLogic(@Param(value = "id") Long id, @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);
}
