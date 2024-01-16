package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.company.CompanyFindSelectDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindSelectDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.service.CompanyService;
import com.mexico.sas.admin.api.service.EmployeeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {

  @Autowired
  private EmployeeService service;

  @PostMapping(headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST", value = "Servicio para crear empleado", nickname = "save")
  public ResponseEntity<EmployeeDto> save(@Valid @RequestBody EmployeeDto employeeDto) throws CustomException {
    log.info("Saving employee");
    service.save(employeeDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(employeeDto);
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT", value = "Servicio para actualizar catalogo", nickname = "update")
  public ResponseEntity<EmployeeDto> update(
          @PathVariable("id") Long id, @Valid @RequestBody EmployeeDto catalogDto) throws CustomException {
    log.info("Updating catalog");
    return ResponseEntity.ok().body(service.update(id, catalogDto));
  }

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

  @GetMapping("/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT", value = "Servicio para recuperar empleado por id", nickname = "update")
  public ResponseEntity<EmployeeFindDto> findById(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding catalog by id");
    return ResponseEntity.ok(service.findById(id));
  }

  @GetMapping()
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar todos los empleados", nickname = "/findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = EmployeeFindDto.class, responseContainer = "List") })
  public ResponseEntity<Page<EmployeeFindDto>> findAll(Pageable pageable) throws CustomException {
    log.info("Getting all employees");
    return ResponseEntity.ok(service.findAll(pageable));
  }

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
