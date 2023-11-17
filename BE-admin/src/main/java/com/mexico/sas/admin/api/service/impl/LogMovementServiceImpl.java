package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.log.*;
import com.mexico.sas.admin.api.dto.user.UserFindDto;
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

    private List<LogMovementDto> parseLogMovementList(Page<LogMovement> logMovements) throws CustomException {
        final List<LogMovementDto> logMovementDtos = new ArrayList<>();
        logMovements.forEach( logMovement -> {
            try {
                LogMovementDto logMovementDto = new LogMovementDto();
                logMovementDto.setDate(logMovement.getCreationDate());
                UserFindDto userDto = userService.findById(logMovement.getUserId());
                logMovementDto.setUserName(buildFullname(userDto.getName(), userDto.getSurname()));
                logMovementDto.setEvent(catalogService.findByIdAndCatalogParent(logMovement.getEventId(), CatalogKeys.LOG_EVENT).getValue());
                logMovementDto.setDetail(catalogService.findByIdAndCatalogParent(logMovement.getDetailId(), CatalogKeys.LOG_DETAIL).getValue());
                logMovementDto.setDescription(logMovement.getDescription());
                logMovementDtos.add(logMovementDto);
            } catch (Exception e) {
                log.warn("Log Movement id {} not added to list, error parsing: {}", logMovement.getId(), e.getMessage());
            }
        } );
        return logMovementDtos;
    }
}
