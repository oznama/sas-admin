package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.role.RoleDto;
import com.mexico.sas.admin.api.dto.role.RoleFindDto;
import com.mexico.sas.admin.api.exception.CustomException;
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

}
