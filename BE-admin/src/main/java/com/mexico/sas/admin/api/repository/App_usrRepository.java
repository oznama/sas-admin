package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.App_usr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface App_usrRepository  extends JpaRepository<App_usr, Long> {

}
