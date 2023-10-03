package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.RolePermissionDto;
import com.mexico.sas.admin.api.dto.RolePermissionsEnaDisDto;
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

//  @PostMapping(headers = "Accept=application/json", path = "/save")
//  @ResponseStatus(code = HttpStatus.CREATED)
//  @ApiOperation(httpMethod = "POST",
//      value = "Servicio para crear relacion rol con permisos",
//      response = RolePermissionDto.class,
//      nickname = "/save")
  public ResponseEntity<?> save(@RequestBody RolePermissionDto rolePermissionDto) throws CustomException {
    log.info("Saving role-permission row");
    service.save(rolePermissionDto);
    log.info("Role-permission saved!");
    return ResponseEntity.status(HttpStatus.CREATED).body(rolePermissionDto);
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

//  @DeleteMapping("/{id}")
//  @ResponseStatus(code = HttpStatus.OK)
//  @ApiOperation(httpMethod = "DELETE",
//          value = "Servicio para eliminar permiso de un rol",
//          nickname = "delete")
  public ResponseEntity<?> delete(@PathVariable("id") Long id) throws CustomException {
    log.info("Deleting role-permission");
    service.delete(id);
    return ResponseEntity.ok().build();
  }

}
