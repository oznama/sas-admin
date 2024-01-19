package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.employee.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
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
  public ResponseEntity<EmployeeFindDto> save(@Valid @RequestBody EmployeeDto employeeDto) throws CustomException {
    log.info("Saving employee");
    return ResponseEntity.status(HttpStatus.CREATED).body(service.save(employeeDto));
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT", value = "Servicio para actualizar empleado", nickname = "update")
  public ResponseEntity<EmployeeUpdateDto> update(
          @PathVariable("id") Long id, @Valid @RequestBody EmployeeUpdateDto employeeUpdateDto) throws CustomException {
    log.info("Updating catalog");
    return ResponseEntity.ok().body(service.update(id, employeeUpdateDto));
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "DELETE", value = "Servicio para eliminar empleado", nickname = "delete")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> delete(@PathVariable("id") Long id) throws CustomException {
    log.info("Delete employee");
    service.deleteLogic(id);
    return ResponseEntity.ok(
            new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }

  @GetMapping("/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar empleado por id", nickname = "update")
  public ResponseEntity<EmployeeFindDto> findById(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding catalog by id");
    return ResponseEntity.ok(service.findById(id));
  }

  @GetMapping()
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar todos los empleados", nickname = "/findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = EmployeePaggeableDto.class, responseContainer = "List") })
  public ResponseEntity<Page<EmployeePaggeableDto>> findAll(@RequestParam(required = false) String filter,
                                                       @RequestParam(required = false) Long companyId,
                                                       Pageable pageable) throws CustomException {
    log.info("Getting all employees");
    return ResponseEntity.ok(service.findAll(filter, companyId, pageable));
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

  @GetMapping("/select/{companyId}")
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar empleados por compania para select", nickname = "/selectByCompanyId")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = EmployeeFindSelectDto.class, responseContainer = "List") })
  public ResponseEntity<List<EmployeeFindSelectDto>> selectByCompanyId(@PathVariable("companyId") Long companyId) throws CustomException {
    log.info("Getting catalog employee");
    return ResponseEntity.ok(service.getForSelect(companyId));
  }

}
