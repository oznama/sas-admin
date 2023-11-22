package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.company.CompanyFindSelectDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindSelectDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.service.CompanyService;
import com.mexico.sas.admin.api.service.EmployeeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {

  @Autowired
  private EmployeeService service;

//  @PostMapping(headers = "Accept=application/json")
//  @ResponseStatus(code = HttpStatus.CREATED)
//  @ApiOperation(httpMethod = "POST", value = "Servicio para crear cliente", nickname = "save")
//  public ResponseEntity<CatalogDto> save(@Valid @RequestBody ClientDto clientDto) throws CustomException {
//    log.info("Saving client");
//    service.save(clientDto);
//    return ResponseEntity.status(HttpStatus.CREATED).body(clientDto);
//  }

//  @PutMapping(path = "/{id}", headers = "Accept=application/json")
//  @ResponseStatus(code = HttpStatus.OK)
//  @ApiOperation(httpMethod = "PUT", value = "Servicio para actualizar catalogo", nickname = "update")
//  public ResponseEntity<CatalogDto> update(
//          @PathVariable("id") Long id, @Valid @RequestBody CatalogUpdateDto catalogDto) throws CustomException {
//    log.info("Updating catalog");
//    return ResponseEntity.ok().body(service.update(id, catalogDto));
//  }

//  @PatchMapping
//  @ResponseStatus(code = HttpStatus.OK)
//  @ApiOperation(httpMethod = "PATCH", value = "Servicio para actualizar status de catalogo", nickname = "updateStatus")
//  @ApiResponses(value = {
//          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
//  })
//  public ResponseEntity<ResponseDto> updateStatus(@Valid @RequestBody UpdateStatusDto updateStatusDto) throws CustomException {
//    log.info("Updating catalog's status");
//    service.updateStatus(updateStatusDto);
//    return ResponseEntity.ok(
//            new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), updateStatusDto));
//  }

//  @GetMapping("/{id}")
//  @ApiIgnore
//  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar catalogo por id", nickname = "findById")
//  public ResponseEntity<CatalogDto> findById(@PathVariable("id") Long id) throws CustomException {
//    log.info("Finding catalog by id");
//    return ResponseEntity.ok(service.findById(id));
//  }

  @GetMapping("/select")
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar empleados para select", nickname = "/select")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = EmployeeFindSelectDto.class, responseContainer = "List") })
  public ResponseEntity<List<EmployeeFindSelectDto>> select() throws CustomException {
    log.info("Getting catalog employee");
    return ResponseEntity.ok(service.getForSelect(false));
  }

  @GetMapping("/select/developers")
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar desarrolladores para select", nickname = "/select")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = EmployeeFindSelectDto.class, responseContainer = "List") })
  public ResponseEntity<List<EmployeeFindSelectDto>> selectDevelopers() throws CustomException {
    log.info("Getting catalog employee");
    return ResponseEntity.ok(service.getForSelect(true));
  }

}
