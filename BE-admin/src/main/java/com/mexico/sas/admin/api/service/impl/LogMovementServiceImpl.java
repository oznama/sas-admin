package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.log.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.*;
import com.mexico.sas.admin.api.repository.LogMovementRepository;
import com.mexico.sas.admin.api.service.*;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oziel Naranjo
 */
@Slf4j
@Service
public class LogMovementServiceImpl extends Utils implements LogMovementService {

    @Autowired
    private LogMovementRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private CatalogService catalogService;

    @Override
    public LogMovementDto findFirstMovement(String tableName, Long recordId) {
        return parseEntityToDto(repository.findFirstByTableNameAndRecordIdOrderByCreationDateAsc(tableName, recordId).orElse(null));
    }

    @Override
    public LogMovementDto findLastMovement(String tableName, Long recordId) {
        return parseEntityToDto(repository.findFirstByTableNameAndRecordIdOrderByCreationDateDesc(tableName, recordId).orElse(null));
    }

    @Override
    public Page<LogMovementDto> findByTableAndRecordId(String tableName, Long recordId, Pageable pageable) throws CustomException {
        return parseLogMovementList(repository.findByTableNameAndRecordId(tableName, recordId, pageable), pageable);
    }

    @Override
    public List<LogMovementDto> findByTableAndRecordId(String tableName, Long recordId) throws CustomException {
        return parseLogMovementList(repository.findByTableNameAndRecordId(tableName, recordId,
                PageRequest.of(0, 100, Sort.Direction.DESC, "creationDate")));
    }

    private Page<LogMovementDto> parseLogMovementList(Page<LogMovement> logMovements, Pageable pageable) throws CustomException {
        return new PageImpl<>(parseLogMovementList(logMovements), pageable, logMovements.getTotalElements());
    }

    private List<LogMovementDto> parseLogMovementList(Page<LogMovement> logMovements) {
        final List<LogMovementDto> logMovementDtos = new ArrayList<>();
        logMovements.forEach( logMovement -> logMovementDtos.add(parseEntityToDto(logMovement)) );
        return logMovementDtos;
    }

    private LogMovementDto parseEntityToDto(LogMovement logMovement) {
        LogMovementDto logMovementDto = new LogMovementDto();
        if( logMovement != null ) {
            try {
                logMovementDto.setId(logMovement.getId());
                logMovementDto.setDate(logMovement.getCreationDate());
                logMovementDto.setUserName(logMovement.getUserFullname());
                try {
                    logMovementDto.setEvent(catalogService.findByIdAndCatalogParent(logMovement.getEventId(), CatalogKeys.LOG_DETAIL).getValue());
                } catch (Exception e) {
                    log.warn("Error to set event {}, error: {}", logMovement.getEventId(), e.getMessage());
                }
                // TODO Build description with String format
                logMovementDto.setDescription(logMovement.getDescription());
            } catch (Exception e) {
                log.warn("Error to parse entity {}, Error: {}", logMovement.getId(), e.getMessage());
            }
        }
        return logMovementDto;
    }
}
