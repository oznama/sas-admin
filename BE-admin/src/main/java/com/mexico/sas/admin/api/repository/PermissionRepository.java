package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

     Optional<Permission> findByName(String name);

}
