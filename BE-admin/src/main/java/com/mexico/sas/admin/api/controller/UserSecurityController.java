package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.user.security.UserSecurityPasswordDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.service.UserSecurityService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/users/security")
public class  UserSecurityController {

  @Autowired
  private UserSecurityService service;

  @PatchMapping("/{id}/setpswd")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PATCH",
          value = "Servicio para asignar password",
          nickname = "/setpswd")
  public ResponseEntity<?> setPassword(@PathVariable("id") Long id,
                                       @Valid @RequestBody UserSecurityPasswordDto userSecurityPasswordDto) throws CustomException {
    log.info("Setting user password");
    service.setPassword(id, userSecurityPasswordDto);
    return ResponseEntity.ok().build();
  }
}
