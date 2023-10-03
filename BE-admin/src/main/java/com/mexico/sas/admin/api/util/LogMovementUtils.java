package com.mexico.sas.admin.api.util;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.model.LogMovement;
import com.mexico.sas.admin.api.repository.LogMovementRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Oziel Naranjo
 */
@Slf4j
public class LogMovementUtils {

    private final String LOG_MOVEMENT_BEAN = "logMovementRepository";

    private LogMovement buildLogMovement(String tableName, Long recordId, String recordIdComplex, Long userId, Date creationDate, Long eventId, Long detailId) {
        LogMovement logMovement = new LogMovement();
        logMovement.setTableName(tableName);
        logMovement.setRecordId(recordId != null ? String.valueOf(recordId) : recordIdComplex);
        logMovement.setUserId(userId);
        logMovement.setCreationDate(creationDate == null ? new Date() : creationDate);
        logMovement.setEventId(eventId);
        logMovement.setDetailId(detailId);
        return logMovement;
    }

    protected void save(String tableName, Long recordId, String recordIdComplex, Long userId, Date creationDate, Long eventId, Long detailId) {
        try {
            LogMovementRepository logMovementRepository = (LogMovementRepository) BeanUtils.getBean(LOG_MOVEMENT_BEAN);
            logMovementRepository.save(buildLogMovement(tableName, recordId, recordIdComplex, userId, creationDate, eventId, detailId));
        } catch (Exception e) {
            log.error("Error saving in LogMovement, error: {}", e.getMessage());
        }
    }

    protected void saveAll(String tableName, List<Long> recordIds, Long userId, Date creationDate) {
        LogMovementRepository logMovementRepository = (LogMovementRepository) BeanUtils.getBean(LOG_MOVEMENT_BEAN);
        List<LogMovement> logMovements = new ArrayList<>();
        recordIds.forEach(recordId -> {
            try {
                logMovements.add(buildLogMovement(tableName, recordId, null, userId, creationDate, CatalogKeys.LOG_EVENT_INSERT, CatalogKeys.LOG_DETAIL_INSERT));
            } catch (Exception e) {
                log.error("Error adding in LogMovement, error: {}", e.getMessage());
            }
        });
        try {
            logMovementRepository.saveAll(logMovements);
        } catch (Exception e) {
            log.error("Error saving batch in LogMovement, error: {}", e.getMessage());
        }
    }

    protected void saveAll2(String tableName, List<String> recordIds, Long userId, Date creationDate) {
        LogMovementRepository logMovementRepository = (LogMovementRepository) BeanUtils.getBean(LOG_MOVEMENT_BEAN);
        List<LogMovement> logMovements = new ArrayList<>();
        recordIds.forEach(recordId -> {
            try {
                logMovements.add(buildLogMovement(tableName, null, recordId, userId, creationDate, CatalogKeys.LOG_EVENT_INSERT, CatalogKeys.LOG_DETAIL_INSERT));
            } catch (Exception e) {
                log.error("Error adding in LogMovement, error: {}", e.getMessage());
            }
        });
        try {
            logMovementRepository.saveAll(logMovements);
        } catch (Exception e) {
            log.error("Error saving batch in LogMovement, error: {}", e.getMessage());
        }
    }

    protected LogMovement getLastMovement(String tableName, Long recordId, String recordIdComplex) {
        LogMovementRepository logMovementRepository = (LogMovementRepository) BeanUtils.getBean(LOG_MOVEMENT_BEAN);
        return logMovementRepository.findFirstByTableNameAndRecordIdOrderByCreationDateDesc(tableName,
                recordId != null ? String.valueOf(recordId) : recordIdComplex).orElse(null);
    }

    protected Date getLastMovementDate(String tableName, Long recordId, String recordIdComplex) {
        LogMovement lastLogMovement = getLastMovement(tableName, recordId, recordIdComplex);
        return lastLogMovement != null ? lastLogMovement.getCreationDate() : null;
    }
}
