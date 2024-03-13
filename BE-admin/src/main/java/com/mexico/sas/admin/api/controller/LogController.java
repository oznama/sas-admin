package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.log.LogMovementDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.service.LogMovementService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/logs")
public class LogController {

  @Autowired
  private LogMovementService service;


  @GetMapping("/{tableName}/{recordId}")
  @ApiOperation(
          httpMethod = "GET", value = "Servicio para recuperar logs por tabla y id", nickname = "findByTableAndRecordId")
  public ResponseEntity<List<LogMovementDto>> findByTableAndRecordId(
          @PathVariable("tableName") String tableName, @PathVariable("recordId") String recordId) throws CustomException {
    log.info("Finding catalog by id and parent");
    return ResponseEntity.ok(service.findByTableAndRecordId(tableName, recordId));
  }

}
