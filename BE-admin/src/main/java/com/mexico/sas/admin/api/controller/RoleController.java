package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeUpdateDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import com.mexico.sas.admin.api.dto.role.RoleFindDto;
import com.mexico.sas.admin.api.dto.role.RoleUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.RoleService;
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
@RequestMapping("/roles")
public class RoleController {

  @Autowired
  private RoleService service;

  @PostMapping(headers = "Accept=application/json", path = "/save")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST",
      value = "Servicio para crear roles",
      response = RoleDto.class,
      nickname = "/save")
  public ResponseEntity<?> save(@RequestBody RoleDto roleDto) throws CustomException {
    log.info("Saving role");
    service.save(roleDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(roleDto);
  }

  @GetMapping("/{id}")
  @ApiOperation(httpMethod = "GET",
      value = "Servicio para recuperar un rol por su id",
      nickname = "/find")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success", response = RoleFindDto.class)
  })
  public ResponseEntity<RoleFindDto> findById(@PathVariable final Long id) throws CustomException {
    log.info("Finding role by id");
    return ResponseEntity.ok(service.findById(id));
  }

  @GetMapping
  @ApiOperation(httpMethod = "GET",
  value = "Servicio para recuperar todos los roles con permisos",
  nickname = "/findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = RoleFindDto.class, responseContainer = "List")
  })
  public ResponseEntity<Page<RoleFindDto>> findAll(Pageable pageable) throws CustomException {
    log.info("Finding all roles");
    return ResponseEntity.ok(service.find(pageable));
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "DELETE", value = "Servicio para eliminar rol", nickname = "delete")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> delete(@PathVariable("id") Long id) throws CustomException {
    log.info("Delete role");
    service.deleteLogic(id);
    return ResponseEntity.ok(
            new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT", value = "Servicio para actualizar rol", nickname = "update")
  public ResponseEntity<RoleUpdateDto> update(
          @PathVariable("id") Long id, @Valid @RequestBody RoleUpdateDto roleUpdateDto) throws CustomException {
    log.info("Updating catalog");
    return ResponseEntity.ok().body(service.update(id, roleUpdateDto));
  }

  @GetMapping("/select")
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar roles para select", nickname = "/select")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = SelectDto.class, responseContainer = "List") })
  public ResponseEntity<List<SelectDto>> getForSelect() throws CustomException {
    log.info("Getting catalog role");
    return ResponseEntity.ok(service.getForSelect());
  }

}
