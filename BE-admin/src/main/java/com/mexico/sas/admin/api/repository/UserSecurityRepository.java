package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.User;
import com.mexico.sas.admin.api.model.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {

    Optional<UserSecurity> findByUser(User user);

    Optional<UserSecurity> findByUserAndPassword(User user, String password);

    @Transactional
    @Modifying
    @Query("update UserSecurity us set us.emailVerified = true where us.id = :id")
    void setEmailVerified(@Param(value = "id") Long id);

    @Transactional
    @Modifying
    @Query("update UserSecurity us set us.password = :password where us.id = :id")
    void setPassword(@Param(value = "id") Long id, @Param(value = "password") String password);

    @Transactional
    @Modifying
    @Query("update UserSecurity us set us.password = :password, us.emailVerified = true where us.id = :id")
    void setPasswordAndEmailVerified(@Param(value = "id") Long id, @Param(value = "password") String password);

}
