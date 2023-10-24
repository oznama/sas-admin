package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

}