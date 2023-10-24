package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.AppUsr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUsrRepository extends JpaRepository<AppUsr, Long> {

}
