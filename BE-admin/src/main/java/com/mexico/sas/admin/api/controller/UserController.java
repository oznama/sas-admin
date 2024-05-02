package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.user.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.UserService;
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
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService service;

  @PostMapping(headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST",
      value = "Servicio para crear usuario",
      nickname = "save")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Success", response = UserFindDto.class)
  })
  public ResponseEntity<UserFindDto> save(@Valid @RequestBody UserDto userDto) throws CustomException {
    log.info("Saving user");
    return ResponseEntity.status(HttpStatus.CREATED).body(service.save(userDto));
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT",
      value = "Servicio para actualizar usuario",
      nickname = "update")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> update(@PathVariable("id") Long id, @RequestBody UserUpdateDto userUpdateDto) throws CustomException {
    log.info("Updating user");
    service.update(id, userUpdateDto);
    return ResponseEntity.ok(new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "DELETE", value = "Servicio para eliminar usuario", nickname = "delete")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> delete(@PathVariable("id") Long id) throws CustomException {
    log.info("Deleting user");
    service.deleteLogic(id);
    return ResponseEntity.ok(new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }

  @GetMapping("/{id}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar usuario por id",
          nickname = "findById")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = UserFindDto.class)
  })
  public ResponseEntity<UserFindDto> findById(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding users by id");
    return ResponseEntity.ok(service.findById(id));
  }

  @GetMapping
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar todos los usuarios por filtro",
          nickname = "findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = UserPaggeableDto.class, responseContainer = "List")
  })
  public ResponseEntity<Page<UserPaggeableDto>> findAll(@RequestParam(required = false) String filter,
                                                        @RequestParam(required = false) Boolean active,
                                                        Pageable pageable) throws CustomException {
    log.info("Finding all users");
    return ResponseEntity.ok(service.findAll(filter, active, pageable));
  }

  @PatchMapping(path = "/{id}/resetPswd")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PATCH", value = "Servicio para resetear password", nickname = "resetPswd")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> resetPswd(@PathVariable("id") Long id) throws CustomException {
    log.info("Reset password");
    service.resetPswd(id);
    return ResponseEntity.ok(new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }

}
