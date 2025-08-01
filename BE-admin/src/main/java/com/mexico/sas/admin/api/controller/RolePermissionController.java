package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.permission.PermissionFindDto;
import com.mexico.sas.admin.api.dto.role.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.RolePermissionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles/permissions")
public class RolePermissionController {

  @Autowired
  private RolePermissionService service;

  @PostMapping(headers = "Accept=application/json", path = "/save")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST",
      value = "Servicio para crear relacion rol con permisos",
      response = RolePermissionDto.class,
      nickname = "/save")
  public ResponseEntity<?> save(@RequestBody RolePermissionDto rolePermissionDto) throws CustomException {
    log.info("Saving role-permission row");
    service.save(rolePermissionDto);
    log.info("Role-permission saved!");
    return ResponseEntity.status(HttpStatus.CREATED).body(rolePermissionDto);
  }
/*
  @DeleteMapping(path = "/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "DELETE", value = "Servicio para eliminar permiso de rol", nickname = "delete")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> delete(@PathVariable("id") Long id) throws CustomException {
    log.info("Delete role");
    service.deleteLogic(id);
    return ResponseEntity.ok(
            new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }*/

  @GetMapping("/catalog")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar todos los permisos",
          nickname = "/findAllPermissions")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = PermissionFindDto.class, responseContainer = "List")
  })
  public ResponseEntity<List<PermissionFindDto>> findAllPermissions() {
    log.info("Finding all permissions");
    return ResponseEntity.ok(service.findAllPermissions());
  }

  @GetMapping("/catalog/{roleId}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar todos los permisos por Rol",
          nickname = "/findByRoleId")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = PermissionFindDto.class, responseContainer = "List")
  })
  public ResponseEntity<List<PermissionFindDto>> findByRoleId(@PathVariable("roleId") Long roleId) {
    log.info("Finding all permissions");
    return ResponseEntity.ok(service.findByRoleId(roleId));
  }
/*
  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT", value = "Servicio para actualizar rol", nickname = "update")
  public ResponseEntity<RolePermissionsUpdateDto> update(
          @PathVariable("id") Long id, @Valid @RequestBody RolePermissionsUpdateDto rolePermissionsUpdateDto) throws CustomException {
    log.info("Updating catalog");
    return ResponseEntity.ok().body(service.update(id, rolePermissionsUpdateDto));
  }

  @PatchMapping("/active")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PATCH",
          value = "Servicio para activar/desactivar permisos de un rol",
          nickname = "/active")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> active(@Valid @RequestBody List<RolePermissionsEnaDisDto> list) throws CustomException {
    log.info("Changing role-permissions status");
    ResponseDto responseDto = new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), service.setActive(list));
    return ResponseEntity.ok(responseDto);
  }
*/
  @DeleteMapping("/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "DELETE",
          value = "Servicio para eliminar permiso de un rol",
          nickname = "delete")
  public ResponseEntity<?> delete(@PathVariable("id") Long id) throws CustomException {
    log.info("Deleting role-permission");
    service.delete(id);
    return ResponseEntity.ok().build();
  }

}
