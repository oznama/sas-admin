package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.log.LogMovementDto;
import com.mexico.sas.admin.api.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Oziel Naranjo
 */
public interface LogMovementService {
    LogMovementDto findFirstMovement(String tableName, String recordId);
    LogMovementDto findLastMovement(String tableName, String recordId);
    Page<LogMovementDto> findByTableAndRecordId(String tableName, String recordId, Pageable pageable) throws CustomException;
    List<LogMovementDto> findByTableAndRecordId(String tableName, String recordId) throws CustomException;
}
