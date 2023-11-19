package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.*;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.sso.SSORequestDto;
import com.mexico.sas.admin.api.dto.sso.SSOResponseDto;
import com.mexico.sas.admin.api.dto.sso.SSOUserDto;
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
}
