package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.*;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.exception.CustomException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import com.mexico.sas.admin.api.service.SSOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/sso")
public class SingleSignOutController {

  @Autowired
  private SSOService service;

  @PostMapping(headers = "Accept=application/json", path = "/login")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "POST",
      value = "Servicio para autenticar usuarios",
      nickname = "/login")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success", response = SSOResponseDto.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = ResponseDto.class)
  })
  public ResponseEntity<SSOResponseDto> login(@Valid @RequestBody final SSORequestDto ssoRequestDto) throws CustomException {
    log.info("Calling REST Service Login...");
    return ResponseEntity.ok(service.login(ssoRequestDto));
  }

  @ApiIgnore
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para validar token",
          nickname = "/validateTokem")
  @GetMapping("validateToken")
  public ResponseEntity<Boolean> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                         @RequestHeader(GeneralKeys.HEADER_VALIDATE_URL) String validateUrl,
                                         @RequestHeader(GeneralKeys.HEADER_VALIDATE_METHOD) String method) throws CustomException {
    log.info("Calling REST Service Valiadte token...");
    return ResponseEntity.ok(service.validateToken(authorization.replace(GeneralKeys.CONSTANT_BEARER, ""),
            validateUrl, method));
  }

  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para desauntenticar usuarios",
          nickname = "/logout")
  @GetMapping("logout")
  public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws CustomException {
    log.info("Calling REST Service Log Out...");
    service.logout(authorization.replace(GeneralKeys.CONSTANT_BEARER, ""));
    return ResponseEntity.ok().build();
  }

  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar informacion de usuario",
          nickname = "/info")
  @GetMapping("info")
  public ResponseEntity<SSOUserDto> getInfoUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws CustomException {
    log.info("Calling REST Service User info...");
    return ResponseEntity.ok(service.getUserInformation(authorization.replace(GeneralKeys.CONSTANT_BEARER, "")));
  }

}
