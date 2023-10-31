package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Requisicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisicionRepository extends JpaRepository<Requisicion, Long> {

}