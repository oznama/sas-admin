package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Transactional
    @Modifying
    @Query("update Role e set e.eliminate = :eliminate, e.active = :active where e.id = :id")
    void deleteLogic(@Param(value = "id") Long id, @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);

}
