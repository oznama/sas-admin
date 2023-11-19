package com.mexico.sas.admin.api.util;

import com.mexico.sas.admin.api.dto.SecurityContextPrincipal;
import com.mexico.sas.admin.api.model.LogMovement;
import com.mexico.sas.admin.api.repository.LogMovementRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Oziel Naranjo
 */
@Slf4j
public class LogMovementUtils extends Utils {

    private final String LOG_MOVEMENT_BEAN = "logMovementRepository";

    private LogMovement buildLogMovement(String tableName, Long recordId, Long eventId, String description) {
        SecurityContextPrincipal principal = getCurrentUser();
        LogMovement logMovement = new LogMovement();
        logMovement.setTableName(tableName);
        logMovement.setRecordId(recordId);
        logMovement.setCreatedBy(principal.getUserId());
        logMovement.setUserFullname(principal.getName());
        logMovement.setEventId(eventId);
        logMovement.setDescription(description);
        return logMovement;
    }

    protected void save(String tableName, Long recordId, Long eventId, String description) {
        try {
            LogMovementRepository logMovementRepository = (LogMovementRepository) BeanUtils.getBean(LOG_MOVEMENT_BEAN);
            logMovementRepository.save(buildLogMovement(tableName, recordId, eventId, description));
        } catch (Exception e) {
            log.error("Error saving in LogMovement, error: {}", e.getMessage());
        }
    }
}
