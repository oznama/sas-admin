package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.LogMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Oziel Naranjo
 */
@Repository
public interface LogMovementRepository extends JpaRepository<LogMovement, Long> {

    Optional<LogMovement> findFirstByTableNameAndRecordIdOrderByCreationDateDesc(String tableName, Long recordId);

    Page<LogMovement> findByTableNameAndRecordId(String tableName, Long recordId, Pageable pageable);

}
