package com.mexico.sas.admin.api.util;

import com.mexico.sas.admin.api.model.LogMovement;
import com.mexico.sas.admin.api.repository.LogMovementRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author Oziel Naranjo
 */
@Slf4j
public class LogMovementUtils {

    private final String LOG_MOVEMENT_BEAN = "logMovementRepository";

    private LogMovement buildLogMovement(String tableName, Long recordId, Long userId, Date creationDate, Long eventId, Long detailId) {
        LogMovement logMovement = new LogMovement();
        logMovement.setTableName(tableName);
        logMovement.setRecordId(recordId);
        logMovement.setUserId(userId);
        logMovement.setCreationDate(creationDate == null ? new Date() : creationDate);
        logMovement.setEventId(eventId);
        logMovement.setDetailId(detailId);
        return logMovement;
    }

    protected void save(String tableName, Long recordId, Long userId, Date creationDate, Long eventId, Long detailId) {
        try {
            LogMovementRepository logMovementRepository = (LogMovementRepository) BeanUtils.getBean(LOG_MOVEMENT_BEAN);
            // TODO Make mechanism for description detail
            logMovementRepository.save(buildLogMovement(tableName, recordId, userId, creationDate, eventId, detailId));
        } catch (Exception e) {
            log.error("Error saving in LogMovement, error: {}", e.getMessage());
        }
    }

    protected LogMovement getLastMovement(String tableName, Long recordId) {
        LogMovementRepository logMovementRepository = (LogMovementRepository) BeanUtils.getBean(LOG_MOVEMENT_BEAN);
        return logMovementRepository.findFirstByTableNameAndRecordIdOrderByCreationDateDesc(tableName, recordId).orElse(null);
    }

    protected Date getLastMovementDate(String tableName, Long recordId) {
        LogMovement lastLogMovement = getLastMovement(tableName, recordId);
        return lastLogMovement != null ? lastLogMovement.getCreationDate() : null;
    }
}
