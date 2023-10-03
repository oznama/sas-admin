package com.mexico.sas.admin.api.repository;

import java.util.Collection;

import com.mexico.sas.admin.api.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RolePermisionRepository extends JpaRepository<RolePermission, Long> {

  Collection<RolePermission> findByRoleId(Long roleId);

  @Transactional
  @Modifying
  @Query("update RolePermission rp set rp.active = :active where rp.id = :id")
  void setActive(@Param(value = "id") Long id, @Param(value = "active") Boolean active);

}
